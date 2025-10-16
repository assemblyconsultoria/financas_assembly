package com.financas.assembly.repository;

import com.financas.assembly.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository for Role entity.
 * Provides methods for role management and queries.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name.
     *
     * @param name role name
     * @return optional role
     */
    Optional<Role> findByName(String name);

    /**
     * Find role by name with permissions eagerly fetched.
     *
     * @param name role name
     * @return optional role with permissions
     */
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    /**
     * Check if role name exists.
     *
     * @param name role name
     * @return true if exists
     */
    boolean existsByName(String name);

    /**
     * Find all active roles.
     *
     * @return list of active roles
     */
    List<Role> findByActiveTrue();

    /**
     * Find all inactive roles.
     *
     * @return list of inactive roles
     */
    List<Role> findByActiveFalse();

    /**
     * Find roles by IDs with permissions.
     *
     * @param ids role IDs
     * @return set of roles with permissions
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id IN :ids")
    Set<Role> findByIdInWithPermissions(@Param("ids") Set<Long> ids);

    /**
     * Find all roles with permissions.
     *
     * @return list of roles with permissions
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions")
    List<Role> findAllWithPermissions();

    /**
     * Count active roles.
     *
     * @return count of active roles
     */
    long countByActiveTrue();

    /**
     * Find roles by permission name.
     *
     * @param permissionName permission name
     * @return list of roles
     */
    @Query("SELECT DISTINCT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * Search roles by name or description.
     *
     * @param searchTerm search term
     * @return list of roles
     */
    @Query("SELECT r FROM Role r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Role> searchByNameOrDescription(@Param("searchTerm") String searchTerm);
}
