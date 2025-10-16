package com.financas.assembly.dto;

import com.financas.assembly.entity.Transacao;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Transacao entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoDTO {

    private Long id;

    @NotNull(message = "Tipo da transação é obrigatório")
    private Transacao.TipoTransacao tipo;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data da transação é obrigatória")
    private LocalDate dataTransacao;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 200, message = "Descrição deve ter entre 3 e 200 caracteres")
    private String descricao;

    @Size(max = 1000, message = "Observações deve ter no máximo 1000 caracteres")
    private String observacoes;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    private String categoriaNome;

    private Long clienteId;

    private String clienteNome;

    @NotNull(message = "Método de pagamento é obrigatório")
    private Transacao.MetodoPagamento metodoPagamento;

    @NotNull(message = "Status é obrigatório")
    private Transacao.StatusTransacao status;

    private Boolean efetivada;

    private LocalDateTime dataEfetivacao;

    @Size(max = 100, message = "Número do documento deve ter no máximo 100 caracteres")
    private String numeroDocumento;

    private Boolean recorrente;

    private Transacao.FrequenciaRecorrencia frequenciaRecorrencia;

    private LocalDate dataFimRecorrencia;

    private Long transacaoPaiId;

}
