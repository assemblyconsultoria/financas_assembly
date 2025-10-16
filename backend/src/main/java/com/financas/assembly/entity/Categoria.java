package com.financas.assembly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing transaction categories.
 * Used to classify financial transactions (e.g., Salary, Rent, Food, etc.).
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categoria extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(min = 2, max = 100, message = "Nome da categoria deve ter entre 2 e 100 caracteres")
    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;

    @NotNull(message = "Tipo da categoria é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCategoria tipo;

    @Column(name = "cor", length = 7)
    private String cor; // Hex color code (e.g., #FF5733)

    @Column(name = "icone", length = 50)
    private String icone; // Icon name or class

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    private Categoria categoriaPai;

    /**
     * Enum for category types (income or expense).
     */
    public enum TipoCategoria {
        RECEITA,  // Income
        DESPESA   // Expense
    }

}
