package com.financas.assembly.repository;

import com.financas.assembly.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cliente entity.
 * Provides CRUD operations and custom queries for client management.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Find all active clients.
     */
    List<Cliente> findByAtivoTrue();

    /**
     * Find all inactive clients.
     */
    List<Cliente> findByAtivoFalse();

    /**
     * Find client by email.
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Find clients by name containing (case insensitive).
     */
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    /**
     * Find clients by city.
     */
    List<Cliente> findByCidade(String cidade);

    /**
     * Find clients by state.
     */
    List<Cliente> findByEstado(String estado);

    /**
     * Check if a client with given email exists.
     */
    boolean existsByEmail(String email);

    /**
     * Count active clients.
     */
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true")
    long countActiveClients();

    /**
     * Find clients with transactions in a specific period.
     */
    @Query("SELECT DISTINCT c FROM Cliente c " +
           "JOIN c.transacoes t " +
           "WHERE t.dataTransacao BETWEEN :startDate AND :endDate")
    List<Cliente> findClientsWithTransactionsInPeriod(
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate
    );

}
