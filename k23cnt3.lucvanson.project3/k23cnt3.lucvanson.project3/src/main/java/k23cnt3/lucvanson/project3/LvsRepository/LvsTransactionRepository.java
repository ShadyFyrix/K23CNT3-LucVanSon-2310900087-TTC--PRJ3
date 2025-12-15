package k23cnt3.lucvanson.project3.LvsRepository;

import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionStatus;
import k23cnt3.lucvanson.project3.LvsEntity.LvsTransaction.LvsTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface cho entity LvsTransaction
 * Xử lý truy vấn liên quan đến giao dịch
 */
@Repository
public interface LvsTransactionRepository extends JpaRepository<LvsTransaction, Long> {

        // Tìm transaction theo user
        List<LvsTransaction> findByLvsUser_LvsUserId(Long lvsUserId);

        Page<LvsTransaction> findByLvsUser_LvsUserId(Long lvsUserId, Pageable pageable);

        // Tìm transaction theo type
        List<LvsTransaction> findByLvsType(LvsTransactionType lvsType);

        Page<LvsTransaction> findByLvsType(LvsTransactionType lvsType, Pageable pageable);

        // Tìm transaction theo status
        List<LvsTransaction> findByLvsStatus(LvsTransactionStatus lvsStatus);

        Page<LvsTransaction> findByLvsStatus(LvsTransactionStatus lvsStatus, Pageable pageable);

        // Tìm transaction theo user và type
        List<LvsTransaction> findByLvsUser_LvsUserIdAndLvsType(Long lvsUserId, LvsTransactionType lvsType);

        Page<LvsTransaction> findByLvsUser_LvsUserIdAndLvsType(Long lvsUserId, LvsTransactionType lvsType,
                        Pageable pageable);

        // Tìm transaction theo user và status
        List<LvsTransaction> findByLvsUser_LvsUserIdAndLvsStatus(Long lvsUserId, LvsTransactionStatus lvsStatus);

        Page<LvsTransaction> findByLvsUser_LvsUserIdAndLvsStatus(Long lvsUserId, LvsTransactionStatus lvsStatus,
                        Pageable pageable);

        // Tìm transaction theo type và status
        List<LvsTransaction> findByLvsTypeAndLvsStatus(LvsTransactionType lvsType, LvsTransactionStatus lvsStatus);

        Page<LvsTransaction> findByLvsTypeAndLvsStatus(LvsTransactionType lvsType, LvsTransactionStatus lvsStatus,
                        Pageable pageable);

        // Tìm transaction theo user, type và status
        List<LvsTransaction> findByLvsUser_LvsUserIdAndLvsTypeAndLvsStatus(Long lvsUserId, LvsTransactionType lvsType,
                        LvsTransactionStatus lvsStatus);

        Page<LvsTransaction> findByLvsUser_LvsUserIdAndLvsTypeAndLvsStatus(Long lvsUserId, LvsTransactionType lvsType,
                        LvsTransactionStatus lvsStatus, Pageable pageable);

        // Tìm transaction theo order
        List<LvsTransaction> findByLvsOrder_LvsOrderId(Long lvsOrderId);

        // Tìm transaction theo LvsAdmin approver
        List<LvsTransaction> findByLvsAdminApprover_LvsUserId(Long lvsUserId);

        // Đếm transaction theo user
        Long countByLvsUser_LvsUserId(Long lvsUserId);

        // Đếm transaction theo type
        Long countByLvsType(LvsTransactionType lvsType);

        // Đếm transaction theo status
        Long countByLvsStatus(LvsTransactionStatus lvsStatus);

        // Đếm transaction trong khoảng thời gian
        Long countByLvsCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

        // Lấy transaction mới nhất
        List<LvsTransaction> findByOrderByLvsCreatedAtDesc();

        Page<LvsTransaction> findByOrderByLvsCreatedAtDesc(Pageable pageable);

        // Lấy transaction theo số tiền
        List<LvsTransaction> findByLvsAmountGreaterThan(Double minAmount);

        Page<LvsTransaction> findByLvsAmountGreaterThan(Double minAmount, Pageable pageable);

        // Cập nhật status
        @Modifying
        @Query("UPDATE LvsTransaction t SET t.lvsStatus = :status WHERE t.lvsTransactionId = :transactionId")
        void updateStatus(@Param("transactionId") Long transactionId, @Param("status") LvsTransactionStatus status);

        // Duyệt transaction
        @Modifying
        @Query("UPDATE LvsTransaction t SET t.lvsStatus = 'SUCCESS', t.lvsAdminApprover.lvsUserId = :adminId, t.lvsApprovedAt = :approvedAt WHERE t.lvsTransactionId = :transactionId")
        void approveTransaction(@Param("transactionId") Long transactionId, @Param("adminId") Long adminId,
                        @Param("approvedAt") LocalDateTime approvedAt);

        // Từ chối transaction
        @Modifying
        @Query("UPDATE LvsTransaction t SET t.lvsStatus = 'FAILED', t.lvsAdminApprover.lvsUserId = :adminId, t.lvsApprovedAt = :approvedAt, t.lvsAdminNote = :adminNote WHERE t.lvsTransactionId = :transactionId")
        void rejectTransaction(@Param("transactionId") Long transactionId, @Param("adminId") Long adminId,
                        @Param("approvedAt") LocalDateTime approvedAt, @Param("adminNote") String adminNote);

        // Lấy tổng tiền theo type
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = :type AND t.lvsStatus = 'SUCCESS'")
        Double getTotalAmountByType(@Param("type") LvsTransactionType type);

        // Lấy tổng tiền theo type và khoảng thời gian
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = :type AND t.lvsStatus = 'SUCCESS' AND t.lvsCreatedAt BETWEEN :startDate AND :endDate")
        Double getTotalAmountByTypeAndDate(@Param("type") LvsTransactionType type,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Lấy tổng doanh thu
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType IN ('PURCHASE', 'SALE') AND t.lvsStatus = 'SUCCESS'")
        Double getTotalRevenue();

        // Lấy doanh thu trong khoảng thời gian
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType IN ('PURCHASE', 'SALE') AND t.lvsStatus = 'SUCCESS' AND t.lvsCreatedAt BETWEEN :startDate AND :endDate")
        Double getRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        // Lấy tổng nạp tiền
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = 'DEPOSIT' AND t.lvsStatus = 'SUCCESS'")
        Double getTotalDeposit();

        // Lấy tổng rút tiền
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = 'WITHDRAWAL' AND t.lvsStatus = 'SUCCESS'")
        Double getTotalWithdrawal();

        // Tìm kiếm transaction theo keyword
        @Query("SELECT t FROM LvsTransaction t WHERE " +
                        "t.lvsDescription LIKE %:keyword% OR " +
                        "t.lvsPaymentInfo LIKE %:keyword% OR " +
                        "t.lvsAdminNote LIKE %:keyword%")
        Page<LvsTransaction> searchTransactions(@Param("keyword") String keyword, Pageable pageable);

        // Tìm transaction theo description containing
        Page<LvsTransaction> findByLvsDescriptionContaining(String keyword, Pageable pageable);

        // Tính tổng amount theo type và status
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = :type AND t.lvsStatus = :status")
        Double sumAmountByTypeAndStatus(@Param("type") LvsTransactionType type,
                        @Param("status") LvsTransactionStatus status);

        // Tính tổng amount theo type, status và date range
        @Query("SELECT SUM(t.lvsAmount) FROM LvsTransaction t WHERE t.lvsType = :type AND t.lvsStatus = :status AND t.lvsCreatedAt BETWEEN :start AND :end")
        Double sumAmountByTypeAndStatusAndDateRange(@Param("type") LvsTransactionType type,
                        @Param("status") LvsTransactionStatus status, @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);

        // Lấy top transactions của user
        @Query("SELECT t FROM LvsTransaction t WHERE t.lvsUser.lvsUserId = :userId ORDER BY t.lvsCreatedAt DESC")
        List<LvsTransaction> findTopByLvsUser_LvsUserIdOrderByLvsCreatedAtDesc(@Param("userId") Long userId,
                        Pageable pageable);
}