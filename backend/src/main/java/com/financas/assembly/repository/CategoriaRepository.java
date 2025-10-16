package com.financas.assembly.repository;

import com.financas.assembly.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Categoria entity.
 * Provides CRUD operations and custom queries for category management.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Find category by name.
     */
    Optional<Categoria> findByNome(String nome);

    /**
     * Find all active categories.
     */
    List<Categoria> findByAtivaTrue();

    /**
     * Find categories by type (RECEITA or DESPESA).
     */
    List<Categoria> findByTipo(Categoria.TipoCategoria tipo);

    /**
     * Find active categories by type.
     */
    List<Categoria> findByTipoAndAtivaTrue(Categoria.TipoCategoria tipo);

    /**
     * Find root categories (without parent).
     */
    @Query("SELECT c FROM Categoria c WHERE c.categoriaPai IS NULL")
    List<Categoria> findRootCategories();

    /**
     * Find subcategories of a parent category.
     */
    List<Categoria> findByCategoriaPaiId(Long categoriaPaiId);

    /**
     * Find categories by name containing (case insensitive).
     */
    List<Categoria> findByNomeContainingIgnoreCase(String nome);

    /**
     * Check if category name exists.
     */
    boolean existsByNome(String nome);

    /**
     * Count categories by type.
     */
    long countByTipo(Categoria.TipoCategoria tipo);

    /**
     * Find most used categories (by transaction count).
     */
    @Query("SELECT c, COUNT(t) as transactionCount FROM Categoria c " +
           "LEFT JOIN Transacao t ON t.categoria = c " +
           "GROUP BY c " +
           "ORDER BY transactionCount DESC")
    List<Object[]> findMostUsedCategories();

    /**
     * Find categories with total transaction value.
     */
    @Query("SELECT c, COALESCE(SUM(t.valor), 0) as totalValue FROM Categoria c " +
           "LEFT JOIN Transacao t ON t.categoria = c " +
           "WHERE c.tipo = :tipo AND t.dataTransacao BETWEEN :startDate AND :endDate " +
           "GROUP BY c " +
           "ORDER BY totalValue DESC")
    List<Object[]> findCategoriesWithTotalValue(
        @Param("tipo") Categoria.TipoCategoria tipo,
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );

}
