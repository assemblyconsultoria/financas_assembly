package com.financas.assembly.repository;

import com.financas.assembly.entity.AuditLog;
import com.financas.assembly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for AuditLog entity.
 * Provides methods for audit trail queries and reporting.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by user.
     *
     * @param user user entity
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByUser(User user, Pageable pageable);

    /**
     * Find audit logs by user ID.
     *
     * @param userId user ID
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    /**
     * Find audit logs by action.
     *
     * @param action action name
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByAction(String action, Pageable pageable);

    /**
     * Find audit logs by entity type.
     *
     * @param entityType entity type
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    /**
     * Find audit logs by entity type and entity ID.
     *
     * @param entityType entity type
     * @param entityId entity ID
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageable);

    /**
     * Find audit logs by IP address.
     *
     * @param ipAddress IP address
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByIpAddress(String ipAddress, Pageable pageable);

    /**
     * Find audit logs within date range.
     *
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination info
     * @return page of audit logs
     */
    Page<AuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find audit logs by user and date range.
     *
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination info
     * @return page of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId " +
           "AND al.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);

    /**
     * Find audit logs by entity and date range.
     *
     * @param entityType entity type
     * @param entityId entity ID
     * @param startDate start date
     * @param endDate end date
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType " +
           "AND al.entityId = :entityId AND al.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findEntityHistory(@Param("entityType") String entityType,
                                     @Param("entityId") Long entityId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find recent audit logs.
     *
     * @param limit number of logs to retrieve
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentLogs(Pageable pageable);

    /**
     * Find all distinct actions.
     *
     * @return list of action names
     */
    @Query("SELECT DISTINCT al.action FROM AuditLog al ORDER BY al.action")
    List<String> findAllActions();

    /**
     * Find all distinct entity types.
     *
     * @return list of entity types
     */
    @Query("SELECT DISTINCT al.entityType FROM AuditLog al ORDER BY al.entityType")
    List<String> findAllEntityTypes();

    /**
     * Find all distinct IP addresses.
     *
     * @return list of IP addresses
     */
    @Query("SELECT DISTINCT al.ipAddress FROM AuditLog al WHERE al.ipAddress IS NOT NULL " +
           "ORDER BY al.ipAddress")
    List<String> findAllIpAddresses();

    /**
     * Count logs by action.
     *
     * @param action action name
     * @return count of logs
     */
    long countByAction(String action);

    /**
     * Count logs by user.
     *
     * @param userId user ID
     * @return count of logs
     */
    long countByUserId(Long userId);

    /**
     * Count logs by entity type.
     *
     * @param entityType entity type
     * @return count of logs
     */
    long countByEntityType(String entityType);

    /**
     * Count logs within date range.
     *
     * @param startDate start date
     * @param endDate end date
     * @return count of logs
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get audit statistics by action.
     *
     * @return list of action statistics
     */
    @Query("SELECT al.action, COUNT(al) FROM AuditLog al GROUP BY al.action ORDER BY COUNT(al) DESC")
    List<Object[]> getActionStatistics();

    /**
     * Get audit statistics by entity type.
     *
     * @return list of entity type statistics
     */
    @Query("SELECT al.entityType, COUNT(al) FROM AuditLog al WHERE al.entityType IS NOT NULL " +
           "GROUP BY al.entityType ORDER BY COUNT(al) DESC")
    List<Object[]> getEntityTypeStatistics();

    /**
     * Get daily activity for the last N days.
     *
     * @param daysAgo number of days ago
     * @return list of daily activity
     */
    @Query("SELECT CAST(al.createdAt AS date), COUNT(al) FROM AuditLog al " +
           "WHERE al.createdAt >= :daysAgo GROUP BY CAST(al.createdAt AS date) " +
           "ORDER BY CAST(al.createdAt AS date) DESC")
    List<Object[]> getDailyActivity(@Param("daysAgo") LocalDateTime daysAgo);

    /**
     * Delete old audit logs.
     *
     * @param cutoffDate cutoff date
     */
    void deleteByCreatedAtBefore(LocalDateTime cutoffDate);
}
