package com.financas.assembly.repository;

import com.financas.assembly.entity.Transacao;
import com.financas.assembly.entity.Transacao.StatusTransacao;
import com.financas.assembly.entity.Transacao.TipoTransacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transacao entity.
 * Provides comprehensive queries for transaction management and financial reporting.
 */
@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    /**
     * Find transactions by type.
     */
    List<Transacao> findByTipo(TipoTransacao tipo);

    /**
     * Find transactions by status.
     */
    List<Transacao> findByStatus(StatusTransacao status);

    /**
     * Find transactions by client.
     */
    List<Transacao> findByClienteId(Long clienteId);

    /**
     * Find transactions by category.
     */
    List<Transacao> findByCategoriaId(Long categoriaId);

    /**
     * Find transactions in a date range.
     */
    List<Transacao> findByDataTransacaoBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find transactions by date range with pagination.
     */
    Page<Transacao> findByDataTransacaoBetween(
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );

    /**
     * Find transactions by type and date range.
     */
    List<Transacao> findByTipoAndDataTransacaoBetween(
        TipoTransacao tipo,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Find recurring transactions.
     */
    List<Transacao> findByRecorrenteTrue();

    /**
     * Find pending transactions.
     */
    List<Transacao> findByStatusAndEfetivadaFalse(StatusTransacao status);

    /**
     * Find transactions by description containing.
     */
    List<Transacao> findByDescricaoContainingIgnoreCase(String descricao);

    /**
     * Calculate total by type and date range.
     */
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
           "WHERE t.tipo = :tipo " +
           "AND t.dataTransacao BETWEEN :startDate AND :endDate " +
           "AND t.status = 'CONFIRMADA'")
    BigDecimal calculateTotalByTypeAndDateRange(
        @Param("tipo") TipoTransacao tipo,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calculate balance (income - expenses) for a period.
     */
    @Query("SELECT " +
           "(SELECT COALESCE(SUM(t1.valor), 0) FROM Transacao t1 " +
           " WHERE t1.tipo = 'RECEITA' AND t1.status = 'CONFIRMADA' " +
           " AND t1.dataTransacao BETWEEN :startDate AND :endDate) - " +
           "(SELECT COALESCE(SUM(t2.valor), 0) FROM Transacao t2 " +
           " WHERE t2.tipo = 'DESPESA' AND t2.status = 'CONFIRMADA' " +
           " AND t2.dataTransacao BETWEEN :startDate AND :endDate)")
    BigDecimal calculateBalance(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find transactions grouped by category with totals.
     */
    @Query("SELECT t.categoria, SUM(t.valor) FROM Transacao t " +
           "WHERE t.tipo = :tipo " +
           "AND t.dataTransacao BETWEEN :startDate AND :endDate " +
           "AND t.status = 'CONFIRMADA' " +
           "GROUP BY t.categoria " +
           "ORDER BY SUM(t.valor) DESC")
    List<Object[]> findTotalsByCategory(
        @Param("tipo") TipoTransacao tipo,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find transactions grouped by payment method.
     */
    @Query("SELECT t.metodoPagamento, COUNT(t), SUM(t.valor) FROM Transacao t " +
           "WHERE t.dataTransacao BETWEEN :startDate AND :endDate " +
           "GROUP BY t.metodoPagamento " +
           "ORDER BY SUM(t.valor) DESC")
    List<Object[]> findStatsByPaymentMethod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find daily transaction summary.
     */
    @Query("SELECT t.dataTransacao, t.tipo, SUM(t.valor) FROM Transacao t " +
           "WHERE t.dataTransacao BETWEEN :startDate AND :endDate " +
           "AND t.status = 'CONFIRMADA' " +
           "GROUP BY t.dataTransacao, t.tipo " +
           "ORDER BY t.dataTransacao DESC")
    List<Object[]> findDailySummary(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find monthly transaction summary.
     */
    @Query("SELECT YEAR(t.dataTransacao), MONTH(t.dataTransacao), t.tipo, SUM(t.valor) " +
           "FROM Transacao t " +
           "WHERE t.status = 'CONFIRMADA' " +
           "GROUP BY YEAR(t.dataTransacao), MONTH(t.dataTransacao), t.tipo " +
           "ORDER BY YEAR(t.dataTransacao) DESC, MONTH(t.dataTransacao) DESC")
    List<Object[]> findMonthlySummary();

    /**
     * Find top expenses for a period.
     */
    @Query("SELECT t FROM Transacao t " +
           "WHERE t.tipo = 'DESPESA' " +
           "AND t.dataTransacao BETWEEN :startDate AND :endDate " +
           "AND t.status = 'CONFIRMADA' " +
           "ORDER BY t.valor DESC")
    List<Transacao> findTopExpenses(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    /**
     * Find transactions by client and date range.
     */
    @Query("SELECT t FROM Transacao t " +
           "WHERE t.cliente.id = :clienteId " +
           "AND t.dataTransacao BETWEEN :startDate AND :endDate " +
           "ORDER BY t.dataTransacao DESC")
    List<Transacao> findByClienteAndDateRange(
        @Param("clienteId") Long clienteId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count transactions by status.
     */
    long countByStatus(StatusTransacao status);

    /**
     * Find overdue pending transactions.
     */
    @Query("SELECT t FROM Transacao t " +
           "WHERE t.status = 'PENDENTE' " +
           "AND t.efetivada = false " +
           "AND t.dataTransacao < CURRENT_DATE " +
           "ORDER BY t.dataTransacao ASC")
    List<Transacao> findOverduePendingTransactions();

}
