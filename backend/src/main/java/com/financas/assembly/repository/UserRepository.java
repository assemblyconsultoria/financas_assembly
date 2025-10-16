package com.financas.assembly.repository;

import com.financas.assembly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * Provides methods for user authentication, management, and queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Find user by email (used for login).
     *
     * @param email user's email
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email with roles eagerly fetched.
     *
     * @param email user's email
     * @return optional user with roles
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    /**
     * Find user by email verification token.
     *
     * @param token verification token
     * @return optional user
     */
    Optional<User> findByEmailVerificationToken(String token);

    /**
     * Find user by password reset token.
     *
     * @param token password reset token
     * @return optional user
     */
    Optional<User> findByPasswordResetToken(String token);

    /**
     * Check if email already exists.
     *
     * @param email email to check
     * @return true if exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all active users.
     *
     * @return list of active users
     */
    List<User> findByActiveTrue();

    /**
     * Find all inactive users.
     *
     * @return list of inactive users
     */
    List<User> findByActiveFalse();

    /**
     * Find users by email verified status.
     *
     * @param verified verification status
     * @return list of users
     */
    List<User> findByEmailVerified(Boolean verified);

    /**
     * Find users with expired password reset tokens.
     *
     * @param now current timestamp
     * @return list of users
     */
    @Query("SELECT u FROM User u WHERE u.passwordResetToken IS NOT NULL " +
           "AND u.passwordResetExpiry < :now")
    List<User> findUsersWithExpiredPasswordResetTokens(@Param("now") LocalDateTime now);

    /**
     * Find users with expired email verification tokens.
     *
     * @param now current timestamp
     * @return list of users
     */
    @Query("SELECT u FROM User u WHERE u.emailVerificationToken IS NOT NULL " +
           "AND u.emailVerificationExpiry < :now")
    List<User> findUsersWithExpiredEmailVerificationTokens(@Param("now") LocalDateTime now);

    /**
     * Find locked users.
     *
     * @param now current timestamp
     * @return list of locked users
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL " +
           "AND u.lockedUntil > :now")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);

    /**
     * Find users whose lock has expired.
     *
     * @param now current timestamp
     * @return list of users
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL " +
           "AND u.lockedUntil <= :now")
    List<User> findUsersWithExpiredLocks(@Param("now") LocalDateTime now);

    /**
     * Update last login timestamp.
     *
     * @param userId user ID
     * @param timestamp login timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :timestamp WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    /**
     * Reset login attempts for a user.
     *
     * @param userId user ID
     */
    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = 0, u.lockedUntil = NULL WHERE u.id = :userId")
    void resetLoginAttempts(@Param("userId") Long userId);

    /**
     * Increment login attempts.
     *
     * @param userId user ID
     */
    @Modifying
    @Query("UPDATE User u SET u.loginAttempts = u.loginAttempts + 1 WHERE u.id = :userId")
    void incrementLoginAttempts(@Param("userId") Long userId);

    /**
     * Clear expired verification tokens.
     *
     * @param now current timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerificationToken = NULL, " +
           "u.emailVerificationExpiry = NULL WHERE u.emailVerificationExpiry < :now")
    void clearExpiredEmailVerificationTokens(@Param("now") LocalDateTime now);

    /**
     * Clear expired password reset tokens.
     *
     * @param now current timestamp
     */
    @Modifying
    @Query("UPDATE User u SET u.passwordResetToken = NULL, " +
           "u.passwordResetExpiry = NULL WHERE u.passwordResetExpiry < :now")
    void clearExpiredPasswordResetTokens(@Param("now") LocalDateTime now);

    /**
     * Find users by role name.
     *
     * @param roleName role name
     * @return list of users
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Count active users.
     *
     * @return count of active users
     */
    long countByActiveTrue();

    /**
     * Count users by email verified status.
     *
     * @param verified verification status
     * @return count of users
     */
    long countByEmailVerified(Boolean verified);

    /**
     * Search users by name or email.
     *
     * @param searchTerm search term
     * @return list of users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
}
