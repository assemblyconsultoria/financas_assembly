package com.financas.assembly.repository;

import com.financas.assembly.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Empresa entity.
 * Extends ClienteRepository functionality with PJ-specific queries.
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Find Empresa by CNPJ.
     */
    Optional<Empresa> findByCnpj(String cnpj);

    /**
     * Find Empresa by razão social.
     */
    Optional<Empresa> findByRazaoSocial(String razaoSocial);

    /**
     * Find companies by sector.
     */
    List<Empresa> findBySetor(String setor);

    /**
     * Find companies by size (porte).
     */
    List<Empresa> findByPorte(String porte);

    /**
     * Check if CNPJ already exists.
     */
    boolean existsByCnpj(String cnpj);

    /**
     * Find companies by nome fantasia containing.
     */
    List<Empresa> findByNomeFantasiaContainingIgnoreCase(String nomeFantasia);

    /**
     * Count companies by sector.
     */
    @Query("SELECT e.setor, COUNT(e) FROM Empresa e GROUP BY e.setor")
    List<Object[]> countBySetor();

}
