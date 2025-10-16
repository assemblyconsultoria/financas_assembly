package com.financas.assembly.service;

import com.financas.assembly.dto.PessoaFisicaDTO;
import com.financas.assembly.entity.PessoaFisica;
import com.financas.assembly.repository.PessoaFisicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for PessoaFisica business logic.
 */
@Service
@Transactional
public class PessoaFisicaService {

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    /**
     * List all pessoas físicas.
     */
    public List<PessoaFisicaDTO> listarTodas() {
        return pessoaFisicaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find pessoa física by ID.
     */
    public Optional<PessoaFisicaDTO> buscarPorId(Long id) {
        return pessoaFisicaRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Find pessoa física by CPF.
     */
    public Optional<PessoaFisicaDTO> buscarPorCpf(String cpf) {
        return pessoaFisicaRepository.findByCpf(cpf)
                .map(this::convertToDTO);
    }

    /**
     * Create a new pessoa física.
     */
    public PessoaFisicaDTO criar(PessoaFisicaDTO dto) {
        // Validate CPF
        validarCpf(dto.getCpf());

        // Check if CPF already exists
        if (pessoaFisicaRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF '" + dto.getCpf() + "' já cadastrado");
        }

        // Check if RG already exists (if provided)
        if (dto.getRg() != null && pessoaFisicaRepository.existsByRg(dto.getRg())) {
            throw new IllegalArgumentException("RG '" + dto.getRg() + "' já cadastrado");
        }

        PessoaFisica pessoa = convertToEntity(dto);
        PessoaFisica saved = pessoaFisicaRepository.save(pessoa);
        return convertToDTO(saved);
    }

    /**
     * Update an existing pessoa física.
     */
    public PessoaFisicaDTO atualizar(Long id, PessoaFisicaDTO dto) {
        PessoaFisica pessoa = pessoaFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa física não encontrada com ID: " + id));

        // Validate CPF
        validarCpf(dto.getCpf());

        // Check if CPF is being changed and if new CPF already exists
        if (!pessoa.getCpf().equals(dto.getCpf()) && pessoaFisicaRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF '" + dto.getCpf() + "' já cadastrado");
        }

        // Check if RG is being changed and if new RG already exists
        if (dto.getRg() != null && !dto.getRg().equals(pessoa.getRg()) && pessoaFisicaRepository.existsByRg(dto.getRg())) {
            throw new IllegalArgumentException("RG '" + dto.getRg() + "' já cadastrado");
        }

        // Update fields
        pessoa.setNome(dto.getNome());
        pessoa.setEmail(dto.getEmail());
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setEndereco(dto.getEndereco());
        pessoa.setCidade(dto.getCidade());
        pessoa.setEstado(dto.getEstado());
        pessoa.setCep(dto.getCep());
        pessoa.setAtivo(dto.getAtivo());
        pessoa.setObservacoes(dto.getObservacoes());
        pessoa.setCpf(dto.getCpf());
        pessoa.setRg(dto.getRg());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setProfissao(dto.getProfissao());
        pessoa.setEstadoCivil(dto.getEstadoCivil());

        PessoaFisica updated = pessoaFisicaRepository.save(pessoa);
        return convertToDTO(updated);
    }

    /**
     * Delete a pessoa física.
     */
    public void excluir(Long id) {
        PessoaFisica pessoa = pessoaFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa física não encontrada com ID: " + id));

        pessoaFisicaRepository.delete(pessoa);
    }

    /**
     * Activate a pessoa física.
     */
    public PessoaFisicaDTO ativar(Long id) {
        PessoaFisica pessoa = pessoaFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa física não encontrada com ID: " + id));

        pessoa.setAtivo(true);
        PessoaFisica updated = pessoaFisicaRepository.save(pessoa);
        return convertToDTO(updated);
    }

    /**
     * Deactivate a pessoa física.
     */
    public PessoaFisicaDTO desativar(Long id) {
        PessoaFisica pessoa = pessoaFisicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa física não encontrada com ID: " + id));

        pessoa.setAtivo(false);
        PessoaFisica updated = pessoaFisicaRepository.save(pessoa);
        return convertToDTO(updated);
    }

    /**
     * Validate CPF format and check digit.
     */
    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos numéricos");
        }

        // Check if all digits are the same (invalid CPF)
        if (cpf.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais");
        }

        // Calculate check digits
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) firstDigit = 0;

            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) secondDigit = 0;

            if (Character.getNumericValue(cpf.charAt(9)) != firstDigit ||
                Character.getNumericValue(cpf.charAt(10)) != secondDigit) {
                throw new IllegalArgumentException("CPF inválido: dígitos verificadores incorretos");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("CPF inválido: " + e.getMessage());
        }
    }

    // Converter methods
    private PessoaFisicaDTO convertToDTO(PessoaFisica pessoa) {
        PessoaFisicaDTO dto = new PessoaFisicaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setEmail(pessoa.getEmail());
        dto.setTelefone(pessoa.getTelefone());
        dto.setEndereco(pessoa.getEndereco());
        dto.setCidade(pessoa.getCidade());
        dto.setEstado(pessoa.getEstado());
        dto.setCep(pessoa.getCep());
        dto.setAtivo(pessoa.getAtivo());
        dto.setObservacoes(pessoa.getObservacoes());
        dto.setCpf(pessoa.getCpf());
        dto.setRg(pessoa.getRg());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setProfissao(pessoa.getProfissao());
        dto.setEstadoCivil(pessoa.getEstadoCivil());
        return dto;
    }

    private PessoaFisica convertToEntity(PessoaFisicaDTO dto) {
        PessoaFisica pessoa = new PessoaFisica();
        pessoa.setNome(dto.getNome());
        pessoa.setEmail(dto.getEmail());
        pessoa.setTelefone(dto.getTelefone());
        pessoa.setEndereco(dto.getEndereco());
        pessoa.setCidade(dto.getCidade());
        pessoa.setEstado(dto.getEstado());
        pessoa.setCep(dto.getCep());
        pessoa.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        pessoa.setObservacoes(dto.getObservacoes());
        pessoa.setCpf(dto.getCpf());
        pessoa.setRg(dto.getRg());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setProfissao(dto.getProfissao());
        pessoa.setEstadoCivil(dto.getEstadoCivil());
        return pessoa;
    }

}
