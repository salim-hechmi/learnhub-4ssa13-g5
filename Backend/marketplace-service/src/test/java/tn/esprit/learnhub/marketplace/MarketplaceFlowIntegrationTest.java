package tn.esprit.learnhub.marketplace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.learnhub.marketplace.dto.event.PaymentCompletedEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Scénario complet : création de commande -> paiement refusé -> paiement accepté -> facture -> événement.
 * Profil "standalone" (catalogue simulé) + H2 en mémoire + RabbitTemplate mocké.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"standalone", "test"})
class MarketplaceFlowIntegrationTest {

    private static final String STUDENT = "student-it-0001";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void fullPurchaseFlow() throws Exception {
        // 1. Création : 2 cours -> 59 + 45 = 104.00
        String body = mvc.perform(post("/api/orders")
                        .header("X-User-Id", STUDENT).header("X-User-Roles", "STUDENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseIds\":[1,2]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(104.00))
                .andExpect(jsonPath("$.items[0].courseTitle").value("Développement web : HTML, CSS et JavaScript"))
                .andReturn().getResponse().getContentAsString();
        long orderId = json.readTree(body).get("id").asLong();

        // 2. Paiement refusé (carte 0002) -> 402 + commande FAILED
        mvc.perform(post("/api/payments/checkout")
                        .header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"method\":\"STRIPE\",\"cardNumber\":\"4000000000000002\"}"))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.orderStatus").value("FAILED"));

        // 3. Paiement accepté -> 200 + PAID + facture
        String checkout = mvc.perform(post("/api/payments/checkout")
                        .header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"method\":\"STRIPE\",\"cardNumber\":\"4242424242424242\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAID"))
                .andExpect(jsonPath("$.invoice.invoiceNumber").exists())
                .andReturn().getResponse().getContentAsString();
        JsonNode invoice = json.readTree(checkout).get("invoice");

        // 4. Un PaymentCompleted par cours, publié après commit
        verify(rabbitTemplate, times(2)).convertAndSend(any(String.class), eq("payment.completed"), any(PaymentCompletedEvent.class));

        // 5. Facture consultable, double paiement refusé, double achat refusé
        mvc.perform(get("/api/invoices/" + invoice.get("id").asLong()).header("X-User-Id", STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
        mvc.perform(post("/api/payments/checkout").header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"method\":\"PAYPAL\"}"))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/orders").header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[2]}"))
                .andExpect(status().isConflict());

        // 6. Vérification « Acheteur Vérifié » (Feign community-service)
        mvc.perform(get("/api/orders/check-purchase").param("studentId", STUDENT).param("courseId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchased").value(true));

        // 7. Suppression d'une commande payée interdite
        mvc.perform(delete("/api/orders/" + orderId).header("X-User-Id", STUDENT))
                .andExpect(status().isConflict());
    }

    @Test
    void sameCourseInTwoPendingOrdersCannotBePaidTwice() throws Exception {
        String student = "student-it-0002";
        long first = createOrder(student, "[3]");
        long second = createOrder(student, "[3,5]");

        mvc.perform(post("/api/payments/checkout").header("X-User-Id", student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + first + ",\"method\":\"PAYPAL\"}"))
                .andExpect(status().isOk());

        // Le cours 3 est déjà payé : la 2e commande ne doit pas pouvoir être réglée
        mvc.perform(post("/api/payments/checkout").header("X-User-Id", student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + second + ",\"method\":\"PAYPAL\"}"))
                .andExpect(status().isConflict());
        mvc.perform(get("/api/orders/" + second).header("X-User-Id", student))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.payments.length()").value(0));
    }

    @Test
    void paymentRules() throws Exception {
        String student = "student-it-0003";
        long orderId = createOrder(student, "[4]");

        // Carte obligatoire pour Stripe
        mvc.perform(post("/api/payments/checkout").header("X-User-Id", student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"method\":\"STRIPE\"}"))
                .andExpect(status().isBadRequest());

        // Une carte « refusée » n'a pas d'effet sur un paiement PayPal
        mvc.perform(post("/api/payments/checkout").header("X-User-Id", student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + ",\"method\":\"PAYPAL\",\"cardNumber\":\"4000000000000002\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }

    @Test
    void onlyPlatformCurrencyIsAccepted() throws Exception {
        mvc.perform(post("/api/orders").header("X-User-Id", "student-it-0004")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[1],\"currency\":\"EUR\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/orders").header("X-User-Id", "student-it-0004")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[1],\"currency\":\"TND\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currency").value("TND"));
    }

    private long createOrder(String student, String courseIds) throws Exception {
        String body = mvc.perform(post("/api/orders").header("X-User-Id", student)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":" + courseIds + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asLong();
    }

    @Test
    void validationAndSecurityErrors() throws Exception {
        mvc.perform(post("/api/orders").header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.courseIds").exists())
                .andExpect(jsonPath("$.path").value("/api/orders"));

        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[1]}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/orders").header("X-User-Id", STUDENT).header("X-User-Roles", "STUDENT"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/orders").header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[999]}"))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/orders").header("X-User-Id", STUDENT)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseIds\":[7]}"))
                .andExpect(status().isBadRequest()); // cours DRAFT
    }
}
