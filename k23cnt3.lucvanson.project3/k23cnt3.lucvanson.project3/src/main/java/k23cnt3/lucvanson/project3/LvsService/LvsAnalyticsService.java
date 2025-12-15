package k23cnt3.lucvanson.project3.LvsService;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service interface cho phân tích dữ liệu
 * Xử lý thống kê, báo cáo, biểu đồ
 */
public interface LvsAnalyticsService {

    // Lấy thống kê tổng quan
    Map<String, Long> lvsGetDashboardStats();

    // Lấy thống kê doanh thu
    Map<String, Double> lvsGetRevenueStats(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy thống kê người dùng
    Map<String, Long> lvsGetUserStats(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy thống kê dự án
    Map<String, Long> lvsGetProjectStats(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy thống kê đơn hàng
    Map<String, Long> lvsGetOrderStats(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy biểu đồ đăng ký user
    Map<String, Object> lvsGetUserRegistrationChart(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy biểu đồ doanh thu
    Map<String, Object> lvsGetRevenueChart(LocalDate lvsStartDate, LocalDate lvsEndDate);

    // Lấy biểu đồ loại dự án
    Map<String, Object> lvsGetProjectTypeChart();

    // Lấy biểu đồ phương thức thanh toán
    Map<String, Object> lvsGetPaymentMethodChart();

    // Lấy top 10 dự án bán chạy
    Map<String, Object> lvsGetTopSellingProjects(int lvsLimit);

    // Lấy top 10 user mua nhiều nhất
    Map<String, Object> lvsGetTopBuyers(int lvsLimit);

    // Lấy top 10 user bán nhiều nhất
    Map<String, Object> lvsGetTopSellers(int lvsLimit);

    // Lấy báo cáo hàng ngày
    Map<String, Object> lvsGetDailyReport(LocalDate lvsDate);

    // Lấy báo cáo hàng tháng
    Map<String, Object> lvsGetMonthlyReport(int lvsYear, int lvsMonth);

    // Lấy báo cáo hàng năm
    Map<String, Object> lvsGetYearlyReport(int lvsYear);

}