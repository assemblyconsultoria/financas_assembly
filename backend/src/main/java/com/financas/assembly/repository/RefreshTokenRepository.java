package com.financas.assembly.repository;

import com.financas.assembly.entity.RefreshToken;
import com.financas.assembly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RefreshToken entity.
 * Provides methods for token management and cleanup.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token value.
     *
     * @param token token value
     * @return optional refresh token
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all tokens for a user.
     *
     * @param user user entity
     * @return list of refresh tokens
     */
    List<RefreshToken> findByUser(User user);

    /**
     * Find all tokens for a user ID.
     *
     * @param userId user ID
     * @return list of refresh tokens
     */
    List<RefreshToken> findByUserId(Long userId);

    /**
     * Find all valid (non-revoked and non-expired) tokens for a user.
     *
     * @param userId user ID
     * @param now current timestamp
     * @return list of valid refresh tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId " +
           "AND rt.revoked = false AND rt.expiryDate > :now")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Find expired tokens.
     *
     * @param now current timestamp
     * @return list of expired tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiryDate < :now")
    List<RefreshToken> findExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Find revoked tokens.
     *
     * @return list of revoked tokens
     */
    List<RefreshToken> findByRevokedTrue();

    /**
     * Find tokens by device info.
     *
     * @param deviceInfo device information
     * @return list of refresh tokens
     */
    List<RefreshToken> findByDeviceInfo(String deviceInfo);

    /**
     * Find tokens by IP address.
     *
     * @param ipAddress IP address
     * @return list of refresh tokens
     */
    List<RefreshToken> findByIpAddress(String ipAddress);

    /**
     * Delete expired tokens.
     *
     * @param now current timestamp
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete revoked tokens older than specified date.
     *
     * @param date cutoff date
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true AND rt.revokedAt < :date")
    void deleteOldRevokedTokens(@Param("date") LocalDateTime date);

    /**
     * Revoke all tokens for a user.
     *
     * @param userId user ID
     * @param now current timestamp
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now " +
           "WHERE rt.user.id = :userId AND rt.revoked = false")
    void revokeAllUserTokens(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Revoke all expired tokens.
     *
     * @param now current timestamp
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now " +
           "WHERE rt.expiryDate < :now AND rt.revoked = false")
    void revokeExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Count valid tokens for a user.
     *
     * @param userId user ID
     * @param now current timestamp
     * @return count of valid tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user.id = :userId " +
           "AND rt.revoked = false AND rt.expiryDate > :now")
    long countValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Count expired tokens.
     *
     * @param now current timestamp
     * @return count of expired tokens
     */
    long countByExpiryDateBefore(LocalDateTime now);

    /**
     * Count revoked tokens.
     *
     * @return count of revoked tokens
     */
    long countByRevokedTrue();

    /**
     * Delete all tokens for a user.
     *
     * @param user user entity
     */
    void deleteByUser(User user);

    /**
     * Delete all tokens for a user ID.
     *
     * @param userId user ID
     */
    void deleteByUserId(Long userId);
}
