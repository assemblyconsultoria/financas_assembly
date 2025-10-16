package com.financas.assembly.service;

import com.financas.assembly.dto.TransacaoDTO;
import com.financas.assembly.entity.Transacao;
import com.financas.assembly.repository.CategoriaRepository;
import com.financas.assembly.repository.ClienteRepository;
import com.financas.assembly.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Transacao business logic.
 */
@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * List all transactions with optional date filters.
     */
    public List<TransacaoDTO> listarTodas(LocalDate dataInicio, LocalDate dataFim) {
        List<Transacao> transacoes;

        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim);
        } else {
            transacoes = transacaoRepository.findAll(Sort.by(Sort.Direction.DESC, "dataTransacao"));
        }

        return transacoes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List transactions with pagination.
     */
    public Page<TransacaoDTO> listarPaginadas(int page, int size, LocalDate dataInicio, LocalDate dataFim) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataTransacao"));

        Page<Transacao> transacoes;
        if (dataInicio != null && dataFim != null) {
            transacoes = transacaoRepository.findByDataTransacaoBetween(dataInicio, dataFim, pageable);
        } else {
            transacoes = transacaoRepository.findAll(pageable);
        }

        return transacoes.map(this::convertToDTO);
    }

    /**
     * Find transaction by ID.
     */
    public Optional<TransacaoDTO> buscarPorId(Long id) {
        return transacaoRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * List transactions by type.
     */
    public List<TransacaoDTO> listarPorTipo(Transacao.TipoTransacao tipo) {
        return transacaoRepository.findByTipo(tipo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List transactions by status.
     */
    public List<TransacaoDTO> listarPorStatus(Transacao.StatusTransacao status) {
        return transacaoRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List transactions by client.
     */
    public List<TransacaoDTO> listarPorCliente(Long clienteId) {
        return transacaoRepository.findByClienteId(clienteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List transactions by category.
     */
    public List<TransacaoDTO> listarPorCategoria(Long categoriaId) {
        return transacaoRepository.findByCategoriaId(categoriaId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List recurring transactions.
     */
    public List<TransacaoDTO> listarRecorrentes() {
        return transacaoRepository.findByRecorrenteTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List pending transactions.
     */
    public List<TransacaoDTO> listarPendentes() {
        return transacaoRepository.findByStatusAndEfetivadaFalse(Transacao.StatusTransacao.PENDENTE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List overdue transactions.
     */
    public List<TransacaoDTO> listarAtrasadas() {
        return transacaoRepository.findOverduePendingTransactions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calculate balance for a period.
     */
    public Map<String, Object> calcularSaldo(LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal receitas = transacaoRepository.calculateTotalByTypeAndDateRange(
                Transacao.TipoTransacao.RECEITA, dataInicio, dataFim);
        BigDecimal despesas = transacaoRepository.calculateTotalByTypeAndDateRange(
                Transacao.TipoTransacao.DESPESA, dataInicio, dataFim);
        BigDecimal saldo = transacaoRepository.calculateBalance(dataInicio, dataFim);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("dataInicio", dataInicio);
        resultado.put("dataFim", dataFim);
        resultado.put("totalReceitas", receitas);
        resultado.put("totalDespesas", despesas);
        resultado.put("saldo", saldo);

        return resultado;
    }

    /**
     * Get report grouped by category.
     */
    public List<Map<String, Object>> relatorioPorCategoria(Transacao.TipoTransacao tipo, LocalDate dataInicio, LocalDate dataFim) {
        List<Object[]> results = transacaoRepository.findTotalsByCategory(tipo, dataInicio, dataFim);

        return results.stream()
                .map(row -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("categoria", row[0]);
                    item.put("total", row[1]);
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Create a new transaction.
     */
    public TransacaoDTO criar(TransacaoDTO dto) {
        // Validate categoria exists
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        if (!categoriaRepository.existsById(dto.getCategoriaId())) {
            throw new IllegalArgumentException("Categoria não encontrada com ID: " + dto.getCategoriaId());
        }

        // Validate cliente exists if provided
        if (dto.getClienteId() != null && !clienteRepository.existsById(dto.getClienteId())) {
            throw new IllegalArgumentException("Cliente não encontrado com ID: " + dto.getClienteId());
        }

        // Validate recurring transaction fields
        if (dto.getRecorrente() != null && dto.getRecorrente()) {
            if (dto.getFrequenciaRecorrencia() == null) {
                throw new IllegalArgumentException("Frequência de recorrência é obrigatória para transações recorrentes");
            }
        }

        Transacao transacao = convertToEntity(dto);
        Transacao saved = transacaoRepository.save(transacao);
        return convertToDTO(saved);
    }

    /**
     * Update an existing transaction.
     */
    public TransacaoDTO atualizar(Long id, TransacaoDTO dto) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com ID: " + id));

        // Validate categoria exists
        if (dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        if (!categoriaRepository.existsById(dto.getCategoriaId())) {
            throw new IllegalArgumentException("Categoria não encontrada com ID: " + dto.getCategoriaId());
        }

        // Validate cliente exists if provided
        if (dto.getClienteId() != null && !clienteRepository.existsById(dto.getClienteId())) {
            throw new IllegalArgumentException("Cliente não encontrado com ID: " + dto.getClienteId());
        }

        // Validate recurring transaction fields
        if (dto.getRecorrente() != null && dto.getRecorrente()) {
            if (dto.getFrequenciaRecorrencia() == null) {
                throw new IllegalArgumentException("Frequência de recorrência é obrigatória para transações recorrentes");
            }
        }

        // Update fields
        transacao.setTipo(dto.getTipo());
        transacao.setValor(dto.getValor());
        transacao.setDataTransacao(dto.getDataTransacao());
        transacao.setDescricao(dto.getDescricao());
        transacao.setObservacoes(dto.getObservacoes());
        transacao.setMetodoPagamento(dto.getMetodoPagamento());
        transacao.setStatus(dto.getStatus());
        transacao.setEfetivada(dto.getEfetivada());
        transacao.setDataEfetivacao(dto.getDataEfetivacao());
        transacao.setNumeroDocumento(dto.getNumeroDocumento());
        transacao.setRecorrente(dto.getRecorrente());
        transacao.setFrequenciaRecorrencia(dto.getFrequenciaRecorrencia());
        transacao.setDataFimRecorrencia(dto.getDataFimRecorrencia());

        categoriaRepository.findById(dto.getCategoriaId())
                .ifPresent(transacao::setCategoria);

        if (dto.getClienteId() != null) {
            clienteRepository.findById(dto.getClienteId())
                    .ifPresent(transacao::setCliente);
        } else {
            transacao.setCliente(null);
        }

        Transacao updated = transacaoRepository.save(transacao);
        return convertToDTO(updated);
    }

    /**
     * Delete a transaction.
     */
    public void excluir(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com ID: " + id));

        // Prevent deletion of confirmed transactions
        if (transacao.getStatus() == Transacao.StatusTransacao.CONFIRMADA && transacao.getEfetivada()) {
            throw new IllegalArgumentException("Não é possível excluir transação confirmada e efetivada");
        }

        transacaoRepository.delete(transacao);
    }

    /**
     * Confirm a transaction.
     */
    public TransacaoDTO confirmar(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com ID: " + id));

        if (transacao.getStatus() == Transacao.StatusTransacao.CANCELADA) {
            throw new IllegalArgumentException("Não é possível confirmar transação cancelada");
        }

        transacao.setStatus(Transacao.StatusTransacao.CONFIRMADA);
        transacao.setEfetivada(true);
        transacao.setDataEfetivacao(LocalDateTime.now());

        Transacao updated = transacaoRepository.save(transacao);
        return convertToDTO(updated);
    }

    /**
     * Cancel a transaction.
     */
    public TransacaoDTO cancelar(Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transação não encontrada com ID: " + id));

        if (transacao.getStatus() == Transacao.StatusTransacao.CONFIRMADA && transacao.getEfetivada()) {
            throw new IllegalArgumentException("Não é possível cancelar transação confirmada e efetivada. Use estorno.");
        }

        transacao.setStatus(Transacao.StatusTransacao.CANCELADA);

        Transacao updated = transacaoRepository.save(transacao);
        return convertToDTO(updated);
    }

    // Converter methods
    private TransacaoDTO convertToDTO(Transacao transacao) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(transacao.getId());
        dto.setTipo(transacao.getTipo());
        dto.setValor(transacao.getValor());
        dto.setDataTransacao(transacao.getDataTransacao());
        dto.setDescricao(transacao.getDescricao());
        dto.setObservacoes(transacao.getObservacoes());
        dto.setMetodoPagamento(transacao.getMetodoPagamento());
        dto.setStatus(transacao.getStatus());
        dto.setEfetivada(transacao.getEfetivada());
        dto.setDataEfetivacao(transacao.getDataEfetivacao());
        dto.setNumeroDocumento(transacao.getNumeroDocumento());
        dto.setRecorrente(transacao.getRecorrente());
        dto.setFrequenciaRecorrencia(transacao.getFrequenciaRecorrencia());
        dto.setDataFimRecorrencia(transacao.getDataFimRecorrencia());

        if (transacao.getCategoria() != null) {
            dto.setCategoriaId(transacao.getCategoria().getId());
            dto.setCategoriaNome(transacao.getCategoria().getNome());
        }

        if (transacao.getCliente() != null) {
            dto.setClienteId(transacao.getCliente().getId());
            dto.setClienteNome(transacao.getCliente().getNome());
        }

        if (transacao.getTransacaoPai() != null) {
            dto.setTransacaoPaiId(transacao.getTransacaoPai().getId());
        }

        return dto;
    }

    private Transacao convertToEntity(TransacaoDTO dto) {
        Transacao transacao = new Transacao();
        transacao.setTipo(dto.getTipo());
        transacao.setValor(dto.getValor());
        transacao.setDataTransacao(dto.getDataTransacao());
        transacao.setDescricao(dto.getDescricao());
        transacao.setObservacoes(dto.getObservacoes());
        transacao.setMetodoPagamento(dto.getMetodoPagamento());
        transacao.setStatus(dto.getStatus() != null ? dto.getStatus() : Transacao.StatusTransacao.PENDENTE);
        transacao.setEfetivada(dto.getEfetivada() != null ? dto.getEfetivada() : false);
        transacao.setDataEfetivacao(dto.getDataEfetivacao());
        transacao.setNumeroDocumento(dto.getNumeroDocumento());
        transacao.setRecorrente(dto.getRecorrente() != null ? dto.getRecorrente() : false);
        transacao.setFrequenciaRecorrencia(dto.getFrequenciaRecorrencia());
        transacao.setDataFimRecorrencia(dto.getDataFimRecorrencia());

        if (dto.getCategoriaId() != null) {
            categoriaRepository.findById(dto.getCategoriaId())
                    .ifPresent(transacao::setCategoria);
        }

        if (dto.getClienteId() != null) {
            clienteRepository.findById(dto.getClienteId())
                    .ifPresent(transacao::setCliente);
        }

        if (dto.getTransacaoPaiId() != null) {
            transacaoRepository.findById(dto.getTransacaoPaiId())
                    .ifPresent(transacao::setTransacaoPai);
        }

        return transacao;
    }

}
