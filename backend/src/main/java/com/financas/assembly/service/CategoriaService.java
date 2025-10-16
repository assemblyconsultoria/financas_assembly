package com.financas.assembly.service;

import com.financas.assembly.dto.CategoriaDTO;
import com.financas.assembly.entity.Categoria;
import com.financas.assembly.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Categoria business logic.
 */
@Service
@Transactional
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * List all categories.
     */
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List active categories.
     */
    public List<CategoriaDTO> listarAtivas() {
        return categoriaRepository.findByAtivaTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List categories by type.
     */
    public List<CategoriaDTO> listarPorTipo(Categoria.TipoCategoria tipo) {
        return categoriaRepository.findByTipoAndAtivaTrue(tipo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List root categories (without parent).
     */
    public List<CategoriaDTO> listarCategoriaRaiz() {
        return categoriaRepository.findRootCategories().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * List subcategories of a parent category.
     */
    public List<CategoriaDTO> listarSubcategorias(Long categoriaPaiId) {
        return categoriaRepository.findByCategoriaPaiId(categoriaPaiId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find category by ID.
     */
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Find category by name.
     */
    public Optional<CategoriaDTO> buscarPorNome(String nome) {
        return categoriaRepository.findByNome(nome)
                .map(this::convertToDTO);
    }

    /**
     * Create a new category.
     */
    public CategoriaDTO criar(CategoriaDTO dto) {
        // Check if category name already exists
        if (categoriaRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Categoria com nome '" + dto.getNome() + "' já existe");
        }

        Categoria categoria = convertToEntity(dto);
        Categoria saved = categoriaRepository.save(categoria);
        return convertToDTO(saved);
    }

    /**
     * Update an existing category.
     */
    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));

        // Check if name is being changed and if new name already exists
        if (!categoria.getNome().equals(dto.getNome()) && categoriaRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Categoria com nome '" + dto.getNome() + "' já existe");
        }

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setTipo(dto.getTipo());
        categoria.setCor(dto.getCor());
        categoria.setIcone(dto.getIcone());
        categoria.setAtiva(dto.getAtiva());

        if (dto.getCategoriaPaiId() != null) {
            // Prevent circular reference
            if (dto.getCategoriaPaiId().equals(id)) {
                throw new IllegalArgumentException("Uma categoria não pode ser pai de si mesma");
            }

            Categoria categoriaPai = categoriaRepository.findById(dto.getCategoriaPaiId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria pai não encontrada"));
            categoria.setCategoriaPai(categoriaPai);
        } else {
            categoria.setCategoriaPai(null);
        }

        Categoria updated = categoriaRepository.save(categoria);
        return convertToDTO(updated);
    }

    /**
     * Delete a category.
     */
    public void excluir(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));

        // Check if category has subcategories
        List<Categoria> subcategorias = categoriaRepository.findByCategoriaPaiId(id);
        if (!subcategorias.isEmpty()) {
            throw new IllegalArgumentException("Não é possível excluir categoria com subcategorias");
        }

        categoriaRepository.delete(categoria);
    }

    /**
     * Activate a category.
     */
    public CategoriaDTO ativar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));

        categoria.setAtiva(true);
        Categoria updated = categoriaRepository.save(categoria);
        return convertToDTO(updated);
    }

    /**
     * Deactivate a category.
     */
    public CategoriaDTO desativar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com ID: " + id));

        categoria.setAtiva(false);
        Categoria updated = categoriaRepository.save(categoria);
        return convertToDTO(updated);
    }

    /**
     * Get categories with total transaction value for a period.
     */
    public List<Object[]> obterCategoriasComTotal(Categoria.TipoCategoria tipo, LocalDate dataInicio, LocalDate dataFim) {
        return categoriaRepository.findCategoriesWithTotalValue(tipo, dataInicio, dataFim);
    }

    /**
     * Get most used categories.
     */
    public List<Object[]> obterCategoriaMaisUsadas() {
        return categoriaRepository.findMostUsedCategories();
    }

    // Converter methods
    private CategoriaDTO convertToDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        dto.setTipo(categoria.getTipo());
        dto.setCor(categoria.getCor());
        dto.setIcone(categoria.getIcone());
        dto.setAtiva(categoria.getAtiva());

        if (categoria.getCategoriaPai() != null) {
            dto.setCategoriaPaiId(categoria.getCategoriaPai().getId());
            dto.setCategoriaPaiNome(categoria.getCategoriaPai().getNome());
        }

        return dto;
    }

    private Categoria convertToEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setTipo(dto.getTipo());
        categoria.setCor(dto.getCor());
        categoria.setIcone(dto.getIcone());
        categoria.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : true);

        if (dto.getCategoriaPaiId() != null) {
            Categoria categoriaPai = categoriaRepository.findById(dto.getCategoriaPaiId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoria pai não encontrada"));
            categoria.setCategoriaPai(categoriaPai);
        }

        return categoria;
    }

}
