package tn.esprit.learnhub.marketplace.service;

import org.springframework.data.domain.Pageable;
import tn.esprit.learnhub.marketplace.dto.request.CheckoutRequest;
import tn.esprit.learnhub.marketplace.dto.response.CheckoutResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PaymentResponse;
import tn.esprit.learnhub.marketplace.enums.PaymentMethod;
import tn.esprit.learnhub.marketplace.enums.PaymentStatus;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.util.List;

public interface PaymentService {

    CheckoutResponse checkout(CheckoutRequest request, CurrentUser user);

    PageResponse<PaymentResponse> getPayments(PaymentStatus status, PaymentMethod method, Pageable pageable);

    PaymentResponse getPayment(Long id);

    List<PaymentResponse> getPaymentsByOrder(Long orderId, CurrentUser user);
}
