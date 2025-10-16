package com.financas.assembly.controller;

import com.financas.assembly.dto.EmpresaDTO;
import com.financas.assembly.service.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Empresa management.
 */
@RestController
@RequestMapping("/api/empresas")
@Tag(name = "Empresas", description = "API para gerenciamento de empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    @Operation(summary = "Listar todas as empresas")
    public ResponseEntity<List<EmpresaDTO>> listarTodas() {
        List<EmpresaDTO> empresas = empresaService.listarTodas();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empresa por ID")
    public ResponseEntity<EmpresaDTO> buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    @Operation(summary = "Buscar empresa por CNPJ")
    public ResponseEntity<EmpresaDTO> buscarPorCnpj(@PathVariable String cnpj) {
        return empresaService.buscarPorCnpj(cnpj)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/setor/{setor}")
    @Operation(summary = "Listar empresas por setor")
    public ResponseEntity<List<EmpresaDTO>> listarPorSetor(@PathVariable String setor) {
        List<EmpresaDTO> empresas = empresaService.listarPorSetor(setor);
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/porte/{porte}")
    @Operation(summary = "Listar empresas por porte")
    public ResponseEntity<List<EmpresaDTO>> listarPorPorte(@PathVariable String porte) {
        List<EmpresaDTO> empresas = empresaService.listarPorPorte(porte);
        return ResponseEntity.ok(empresas);
    }

    @PostMapping
    @Operation(summary = "Criar nova empresa")
    public ResponseEntity<?> criar(@Valid @RequestBody EmpresaDTO dto) {
        try {
            EmpresaDTO created = empresaService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empresa")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaDTO dto) {
        try {
            EmpresaDTO updated = empresaService.atualizar(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir empresa")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            empresaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar empresa")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            EmpresaDTO updated = empresaService.ativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar empresa")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            EmpresaDTO updated = empresaService.desativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
