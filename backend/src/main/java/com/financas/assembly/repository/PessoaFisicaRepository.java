package com.financas.assembly.repository;

import com.financas.assembly.entity.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PessoaFisica entity.
 * Extends ClienteRepository functionality with PF-specific queries.
 */
@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {

    /**
     * Find Pessoa Física by CPF.
     */
    Optional<PessoaFisica> findByCpf(String cpf);

    /**
     * Find Pessoa Física by RG.
     */
    Optional<PessoaFisica> findByRg(String rg);

    /**
     * Check if CPF already exists.
     */
    boolean existsByCpf(String cpf);

    /**
     * Check if RG already exists.
     */
    boolean existsByRg(String rg);

}
