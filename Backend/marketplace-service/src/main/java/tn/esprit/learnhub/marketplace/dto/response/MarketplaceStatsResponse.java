package tn.esprit.learnhub.marketplace.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Indicateurs du tableau de bord backoffice. */
public record MarketplaceStatsResponse(
        long totalOrders,
        long pendingOrders,
        long paidOrders,
        long failedOrders,
        long refundedOrders,
        BigDecimal totalRevenue,
        BigDecimal averageBasket,
        String currency,
        long totalPayments,
        long successfulPayments,
        double paymentSuccessRate,
        long invoicesCount,
        List<TopCourse> topCourses
) {
    public record TopCourse(Long courseId, String courseTitle, long salesCount, BigDecimal revenue) {
    }
}
