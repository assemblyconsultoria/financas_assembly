package com.financas.assembly.service;

import com.financas.assembly.dto.EmpresaDTO;
import com.financas.assembly.entity.Empresa;
import com.financas.assembly.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Empresa business logic.
 */
@Service
@Transactional
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * List all empresas.
     */
    public List<EmpresaDTO> listarTodas() {
        return empresaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find empresa by ID.
     */
    public Optional<EmpresaDTO> buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Find empresa by CNPJ.
     */
    public Optional<EmpresaDTO> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(cnpj)
                .map(this::convertToDTO);
    }

    /**
     * List empresas by sector.
     */
    public List<EmpresaDTO> listarPorSetor(String setor) {
        return empresaRepository.findBySetor(setor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List empresas by size (porte).
     */
    public List<EmpresaDTO> listarPorPorte(String porte) {
        return empresaRepository.findByPorte(porte).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a new empresa.
     */
    public EmpresaDTO criar(EmpresaDTO dto) {
        // Validate CNPJ
        validarCnpj(dto.getCnpj());

        // Check if CNPJ already exists
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ '" + dto.getCnpj() + "' já cadastrado");
        }

        Empresa empresa = convertToEntity(dto);
        Empresa saved = empresaRepository.save(empresa);
        return convertToDTO(saved);
    }

    /**
     * Update an existing empresa.
     */
    public EmpresaDTO atualizar(Long id, EmpresaDTO dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        // Validate CNPJ
        validarCnpj(dto.getCnpj());

        // Check if CNPJ is being changed and if new CNPJ already exists
        if (!empresa.getCnpj().equals(dto.getCnpj()) && empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new IllegalArgumentException("CNPJ '" + dto.getCnpj() + "' já cadastrado");
        }

        // Update fields
        empresa.setNome(dto.getNome());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setEndereco(dto.getEndereco());
        empresa.setCidade(dto.getCidade());
        empresa.setEstado(dto.getEstado());
        empresa.setCep(dto.getCep());
        empresa.setAtivo(dto.getAtivo());
        empresa.setObservacoes(dto.getObservacoes());
        empresa.setCnpj(dto.getCnpj());
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setDataFundacao(dto.getDataFundacao());
        empresa.setSetor(dto.getSetor());
        empresa.setPorte(dto.getPorte());
        empresa.setResponsavelNome(dto.getResponsavelNome());
        empresa.setResponsavelEmail(dto.getResponsavelEmail());
        empresa.setResponsavelTelefone(dto.getResponsavelTelefone());

        Empresa updated = empresaRepository.save(empresa);
        return convertToDTO(updated);
    }

    /**
     * Delete an empresa.
     */
    public void excluir(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        empresaRepository.delete(empresa);
    }

    /**
     * Activate an empresa.
     */
    public EmpresaDTO ativar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        empresa.setAtivo(true);
        Empresa updated = empresaRepository.save(empresa);
        return convertToDTO(updated);
    }

    /**
     * Deactivate an empresa.
     */
    public EmpresaDTO desativar(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada com ID: " + id));

        empresa.setAtivo(false);
        Empresa updated = empresaRepository.save(empresa);
        return convertToDTO(updated);
    }

    /**
     * Validate CNPJ format and check digit.
     */
    private void validarCnpj(String cnpj) {
        if (cnpj == null || !cnpj.matches("\\d{14}")) {
            throw new IllegalArgumentException("CNPJ inválido: deve conter 14 dígitos numéricos");
        }

        // Check if all digits are the same (invalid CNPJ)
        if (cnpj.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("CNPJ inválido: todos os dígitos são iguais");
        }

        // Calculate check digits
        try {
            // First check digit
            int[] weight1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight1[i];
            }
            int firstDigit = sum % 11 < 2 ? 0 : 11 - (sum % 11);

            // Second check digit
            int[] weight2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            sum = 0;
            for (int i = 0; i < 13; i++) {
                sum += Character.getNumericValue(cnpj.charAt(i)) * weight2[i];
            }
            int secondDigit = sum % 11 < 2 ? 0 : 11 - (sum % 11);

            if (Character.getNumericValue(cnpj.charAt(12)) != firstDigit ||
                Character.getNumericValue(cnpj.charAt(13)) != secondDigit) {
                throw new IllegalArgumentException("CNPJ inválido: dígitos verificadores incorretos");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("CNPJ inválido: " + e.getMessage());
        }
    }

    // Converter methods
    private EmpresaDTO convertToDTO(Empresa empresa) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(empresa.getId());
        dto.setNome(empresa.getNome());
        dto.setEmail(empresa.getEmail());
        dto.setTelefone(empresa.getTelefone());
        dto.setEndereco(empresa.getEndereco());
        dto.setCidade(empresa.getCidade());
        dto.setEstado(empresa.getEstado());
        dto.setCep(empresa.getCep());
        dto.setAtivo(empresa.getAtivo());
        dto.setObservacoes(empresa.getObservacoes());
        dto.setCnpj(empresa.getCnpj());
        dto.setRazaoSocial(empresa.getRazaoSocial());
        dto.setNomeFantasia(empresa.getNomeFantasia());
        dto.setInscricaoEstadual(empresa.getInscricaoEstadual());
        dto.setInscricaoMunicipal(empresa.getInscricaoMunicipal());
        dto.setDataFundacao(empresa.getDataFundacao());
        dto.setSetor(empresa.getSetor());
        dto.setPorte(empresa.getPorte());
        dto.setResponsavelNome(empresa.getResponsavelNome());
        dto.setResponsavelEmail(empresa.getResponsavelEmail());
        dto.setResponsavelTelefone(empresa.getResponsavelTelefone());
        return dto;
    }

    private Empresa convertToEntity(EmpresaDTO dto) {
        Empresa empresa = new Empresa();
        empresa.setNome(dto.getNome());
        empresa.setEmail(dto.getEmail());
        empresa.setTelefone(dto.getTelefone());
        empresa.setEndereco(dto.getEndereco());
        empresa.setCidade(dto.getCidade());
        empresa.setEstado(dto.getEstado());
        empresa.setCep(dto.getCep());
        empresa.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        empresa.setObservacoes(dto.getObservacoes());
        empresa.setCnpj(dto.getCnpj());
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setDataFundacao(dto.getDataFundacao());
        empresa.setSetor(dto.getSetor());
        empresa.setPorte(dto.getPorte());
        empresa.setResponsavelNome(dto.getResponsavelNome());
        empresa.setResponsavelEmail(dto.getResponsavelEmail());
        empresa.setResponsavelTelefone(dto.getResponsavelTelefone());
        return empresa;
    }

}
