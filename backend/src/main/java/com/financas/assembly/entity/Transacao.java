package com.financas.assembly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing financial transactions.
 * Tracks income and expenses with detailed information.
 */
@Entity
@Table(name = "transacoes", indexes = {
    @Index(name = "idx_transacao_data", columnList = "data_transacao"),
    @Index(name = "idx_transacao_tipo", columnList = "tipo"),
    @Index(name = "idx_transacao_cliente", columnList = "cliente_id"),
    @Index(name = "idx_transacao_categoria", columnList = "categoria_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transacao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tipo da transação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoTransacao tipo;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Valor deve ter no máximo 10 dígitos inteiros e 2 decimais")
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Data da transação é obrigatória")
    @Column(name = "data_transacao", nullable = false)
    private LocalDate dataTransacao;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 200, message = "Descrição deve ter entre 3 e 200 caracteres")
    @Column(name = "descricao", nullable = false, length = 200)
    private String descricao;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "Categoria é obrigatória")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull(message = "Método de pagamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false, length = 30)
    private MetodoPagamento metodoPagamento;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusTransacao status;

    @Column(name = "efetivada", nullable = false)
    private Boolean efetivada = false;

    @Column(name = "data_efetivacao")
    private LocalDateTime dataEfetivacao;

    @Size(max = 100, message = "Número do documento deve ter no máximo 100 caracteres")
    @Column(name = "numero_documento", length = 100)
    private String numeroDocumento;

    @Column(name = "recorrente", nullable = false)
    private Boolean recorrente = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequencia_recorrencia", length = 20)
    private FrequenciaRecorrencia frequenciaRecorrencia;

    @Column(name = "data_fim_recorrencia")
    private LocalDate dataFimRecorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transacao_pai_id")
    private Transacao transacaoPai; // For recurring transactions

    /**
     * Enum for transaction types.
     */
    public enum TipoTransacao {
        RECEITA,  // Income
        DESPESA   // Expense
    }

    /**
     * Enum for payment methods.
     */
    public enum MetodoPagamento {
        DINHEIRO,
        CARTAO_CREDITO,
        CARTAO_DEBITO,
        TRANSFERENCIA,
        PIX,
        BOLETO,
        CHEQUE,
        OUTRO
    }

    /**
     * Enum for transaction status.
     */
    public enum StatusTransacao {
        PENDENTE,
        CONFIRMADA,
        CANCELADA,
        ESTORNADA
    }

    /**
     * Enum for recurring transaction frequency.
     */
    public enum FrequenciaRecorrencia {
        DIARIA,
        SEMANAL,
        QUINZENAL,
        MENSAL,
        BIMESTRAL,
        TRIMESTRAL,
        SEMESTRAL,
        ANUAL
    }

}
