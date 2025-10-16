package com.financas.assembly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Empresa entity.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO extends ClienteDTO {

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    private String cnpj;

    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    private String nomeFantasia;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    private String inscricaoEstadual;

    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    private String inscricaoMunicipal;

    private LocalDate dataFundacao;

    @Size(max = 100, message = "Setor deve ter no máximo 100 caracteres")
    private String setor;

    @Size(max = 20, message = "Porte deve ter no máximo 20 caracteres")
    private String porte;

    @Size(max = 100, message = "Nome do responsável deve ter no máximo 100 caracteres")
    private String responsavelNome;

    @Size(max = 100, message = "Email do responsável deve ter no máximo 100 caracteres")
    private String responsavelEmail;

    @Size(max = 20, message = "Telefone do responsável deve ter no máximo 20 caracteres")
    private String responsavelTelefone;

}
