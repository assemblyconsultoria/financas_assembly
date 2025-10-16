package com.financas.assembly.repository;

import com.financas.assembly.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Permission entity.
 * Provides methods for permission management and queries.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name.
     *
     * @param name permission name
     * @return optional permission
     */
    Optional<Permission> findByName(String name);

    /**
     * Find permission by resource and action.
     *
     * @param resource resource type
     * @param action action type
     * @return optional permission
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Check if permission name exists.
     *
     * @param name permission name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Check if resource and action combination exists.
     *
     * @param resource resource type
     * @param action action type
     * @return true if exists
     */
    boolean existsByResourceAndAction(String resource, String action);

    /**
     * Find all active permissions.
     *
     * @return list of active permissions
     */
    List<Permission> findByActiveTrue();

    /**
     * Find all inactive permissions.
     *
     * @return list of inactive permissions
     */
    List<Permission> findByActiveFalse();

    /**
     * Find permissions by resource.
     *
     * @param resource resource type
     * @return list of permissions
     */
    List<Permission> findByResource(String resource);

    /**
     * Find permissions by resource and active status.
     *
     * @param resource resource type
     * @param active active status
     * @return list of permissions
     */
    List<Permission> findByResourceAndActive(String resource, Boolean active);

    /**
     * Find permissions by action.
     *
     * @param action action type
     * @return list of permissions
     */
    List<Permission> findByAction(String action);

    /**
     * Find all distinct resources.
     *
     * @return list of resource names
     */
    @Query("SELECT DISTINCT p.resource FROM Permission p ORDER BY p.resource")
    List<String> findAllResources();

    /**
     * Find all distinct actions.
     *
     * @return list of action names
     */
    @Query("SELECT DISTINCT p.action FROM Permission p ORDER BY p.action")
    List<String> findAllActions();

    /**
     * Count active permissions.
     *
     * @return count of active permissions
     */
    long countByActiveTrue();

    /**
     * Count permissions by resource.
     *
     * @param resource resource type
     * @return count of permissions
     */
    long countByResource(String resource);

    /**
     * Find permissions by role ID.
     *
     * @param roleId role ID
     * @return list of permissions
     */
    @Query("SELECT DISTINCT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * Find permissions not assigned to a role.
     *
     * @param roleId role ID
     * @return list of permissions
     */
    @Query("SELECT p FROM Permission p WHERE p.id NOT IN " +
           "(SELECT p2.id FROM Permission p2 JOIN p2.roles r WHERE r.id = :roleId)")
    List<Permission> findNotAssignedToRole(@Param("roleId") Long roleId);

    /**
     * Search permissions by name or description.
     *
     * @param searchTerm search term
     * @return list of permissions
     */
    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Permission> searchByNameOrDescription(@Param("searchTerm") String searchTerm);
}
