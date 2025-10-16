package com.financas.assembly.controller;

import com.financas.assembly.dto.TransacaoDTO;
import com.financas.assembly.entity.Transacao;
import com.financas.assembly.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Transacao management.
 */
@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transações", description = "API para gerenciamento de transações financeiras")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as transações")
    public ResponseEntity<List<TransacaoDTO>> listarTodas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        List<TransacaoDTO> transacoes = transacaoService.listarTodas(dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/paginadas")
    @Operation(summary = "Listar transações com paginação")
    public ResponseEntity<Page<TransacaoDTO>> listarPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        Page<TransacaoDTO> transacoes = transacaoService.listarPaginadas(page, size, dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar transação por ID")
    public ResponseEntity<TransacaoDTO> buscarPorId(@PathVariable Long id) {
        return transacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar transações por tipo (RECEITA ou DESPESA)")
    public ResponseEntity<List<TransacaoDTO>> listarPorTipo(@PathVariable Transacao.TipoTransacao tipo) {
        List<TransacaoDTO> transacoes = transacaoService.listarPorTipo(tipo);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar transações por status")
    public ResponseEntity<List<TransacaoDTO>> listarPorStatus(@PathVariable Transacao.StatusTransacao status) {
        List<TransacaoDTO> transacoes = transacaoService.listarPorStatus(status);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar transações por cliente")
    public ResponseEntity<List<TransacaoDTO>> listarPorCliente(@PathVariable Long clienteId) {
        List<TransacaoDTO> transacoes = transacaoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar transações por categoria")
    public ResponseEntity<List<TransacaoDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<TransacaoDTO> transacoes = transacaoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/recorrentes")
    @Operation(summary = "Listar transações recorrentes")
    public ResponseEntity<List<TransacaoDTO>> listarRecorrentes() {
        List<TransacaoDTO> transacoes = transacaoService.listarRecorrentes();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Listar transações pendentes")
    public ResponseEntity<List<TransacaoDTO>> listarPendentes() {
        List<TransacaoDTO> transacoes = transacaoService.listarPendentes();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/atrasadas")
    @Operation(summary = "Listar transações atrasadas (pendentes e vencidas)")
    public ResponseEntity<List<TransacaoDTO>> listarAtrasadas() {
        List<TransacaoDTO> transacoes = transacaoService.listarAtrasadas();
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/relatorio/saldo")
    @Operation(summary = "Calcular saldo (receitas - despesas) para um período")
    public ResponseEntity<Map<String, Object>> calcularSaldo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        Map<String, Object> saldo = transacaoService.calcularSaldo(dataInicio, dataFim);
        return ResponseEntity.ok(saldo);
    }

    @GetMapping("/relatorio/por-categoria")
    @Operation(summary = "Relatório de transações agrupadas por categoria")
    public ResponseEntity<List<Map<String, Object>>> relatorioPorCategoria(
            @RequestParam Transacao.TipoTransacao tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        List<Map<String, Object>> relatorio = transacaoService.relatorioPorCategoria(tipo, dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @PostMapping
    @Operation(summary = "Criar nova transação")
    public ResponseEntity<?> criar(@Valid @RequestBody TransacaoDTO dto) {
        try {
            TransacaoDTO created = transacaoService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoDTO dto) {
        try {
            TransacaoDTO updated = transacaoService.atualizar(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            transacaoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar transação")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        try {
            TransacaoDTO updated = transacaoService.confirmar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar transação")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            TransacaoDTO updated = transacaoService.cancelar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
