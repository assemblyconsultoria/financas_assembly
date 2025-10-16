package com.financas.assembly.controller;

import com.financas.assembly.dto.PessoaFisicaDTO;
import com.financas.assembly.service.PessoaFisicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for PessoaFisica management.
 */
@RestController
@RequestMapping("/api/pessoas-fisicas")
@Tag(name = "Pessoas Físicas", description = "API para gerenciamento de pessoas físicas")
@CrossOrigin(origins = "*")
public class PessoaFisicaController {

    @Autowired
    private PessoaFisicaService pessoaFisicaService;

    @GetMapping
    @Operation(summary = "Listar todas as pessoas físicas")
    public ResponseEntity<List<PessoaFisicaDTO>> listarTodas() {
        List<PessoaFisicaDTO> pessoas = pessoaFisicaService.listarTodas();
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pessoa física por ID")
    public ResponseEntity<PessoaFisicaDTO> buscarPorId(@PathVariable Long id) {
        return pessoaFisicaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar pessoa física por CPF")
    public ResponseEntity<PessoaFisicaDTO> buscarPorCpf(@PathVariable String cpf) {
        return pessoaFisicaService.buscarPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova pessoa física")
    public ResponseEntity<?> criar(@Valid @RequestBody PessoaFisicaDTO dto) {
        try {
            PessoaFisicaDTO created = pessoaFisicaService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pessoa física")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody PessoaFisicaDTO dto) {
        try {
            PessoaFisicaDTO updated = pessoaFisicaService.atualizar(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pessoa física")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            pessoaFisicaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar pessoa física")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            PessoaFisicaDTO updated = pessoaFisicaService.ativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar pessoa física")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            PessoaFisicaDTO updated = pessoaFisicaService.desativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
