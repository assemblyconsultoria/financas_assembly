package com.financas.assembly.controller;

import com.financas.assembly.dto.CategoriaDTO;
import com.financas.assembly.entity.Categoria;
import com.financas.assembly.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Categoria management.
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "API para gerenciamento de categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar categorias ativas")
    public ResponseEntity<List<CategoriaDTO>> listarAtivas() {
        List<CategoriaDTO> categorias = categoriaService.listarAtivas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar categorias por tipo (RECEITA ou DESPESA)")
    public ResponseEntity<List<CategoriaDTO>> listarPorTipo(@PathVariable Categoria.TipoCategoria tipo) {
        List<CategoriaDTO> categorias = categoriaService.listarPorTipo(tipo);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/raiz")
    @Operation(summary = "Listar categorias raiz (sem pai)")
    public ResponseEntity<List<CategoriaDTO>> listarCategoriaRaiz() {
        List<CategoriaDTO> categorias = categoriaService.listarCategoriaRaiz();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/subcategorias/{id}")
    @Operation(summary = "Listar subcategorias de uma categoria")
    public ResponseEntity<List<CategoriaDTO>> listarSubcategorias(@PathVariable Long id) {
        List<CategoriaDTO> categorias = categoriaService.listarSubcategorias(id);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria")
    public ResponseEntity<?> criar(@Valid @RequestBody CategoriaDTO dto) {
        try {
            CategoriaDTO created = categoriaService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        try {
            CategoriaDTO updated = categoriaService.atualizar(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir categoria")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            categoriaService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar categoria")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            CategoriaDTO updated = categoriaService.ativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar categoria")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            CategoriaDTO updated = categoriaService.desativar(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
