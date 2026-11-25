package tn.esprit.learnhub.marketplace.service;

import org.springframework.data.domain.Pageable;
import tn.esprit.learnhub.marketplace.dto.request.CreateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.request.UpdateOrderRequest;
import tn.esprit.learnhub.marketplace.dto.response.MarketplaceStatsResponse;
import tn.esprit.learnhub.marketplace.dto.response.OrderResponse;
import tn.esprit.learnhub.marketplace.dto.response.PageResponse;
import tn.esprit.learnhub.marketplace.dto.response.PurchaseCheckResponse;
import tn.esprit.learnhub.marketplace.enums.OrderStatus;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request, CurrentUser user);

    List<OrderResponse> getMyOrders(CurrentUser user);

    OrderResponse getOrderById(Long id, CurrentUser user);

    PageResponse<OrderResponse> searchOrders(OrderStatus status, String studentId, Long courseId, String q,
                                             LocalDate from, LocalDate to, Pageable pageable);

    OrderResponse updateOrder(Long id, UpdateOrderRequest request, CurrentUser user);

    OrderResponse updateStatus(Long id, OrderStatus newStatus);

    OrderResponse refundOrder(Long id);

    void deleteOrder(Long id, CurrentUser user);

    PurchaseCheckResponse checkPurchase(String studentId, Long courseId);

    MarketplaceStatsResponse getStats();
}
