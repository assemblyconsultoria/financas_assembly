package com.financas.assembly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity representing a corporate client (empresa/pessoa jurídica).
 * Extends Cliente with specific fields for companies like CNPJ and registration info.
 */
@Entity
@DiscriminatorValue("PJ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa extends Cliente {

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos")
    @Column(name = "cnpj", unique = true, nullable = false, length = 14)
    private String cnpj;

    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    @Column(name = "nome_fantasia", length = 200)
    private String nomeFantasia;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;

    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;

    @Column(name = "data_fundacao")
    private LocalDate dataFundacao;

    @Size(max = 100, message = "Setor deve ter no máximo 100 caracteres")
    @Column(name = "setor", length = 100)
    private String setor;

    @Size(max = 20, message = "Porte deve ter no máximo 20 caracteres")
    @Column(name = "porte", length = 20)
    private String porte;

    @Size(max = 100, message = "Nome do responsável deve ter no máximo 100 caracteres")
    @Column(name = "responsavel_nome", length = 100)
    private String responsavelNome;

    @Size(max = 100, message = "Email do responsável deve ter no máximo 100 caracteres")
    @Column(name = "responsavel_email", length = 100)
    private String responsavelEmail;

    @Size(max = 20, message = "Telefone do responsável deve ter no máximo 20 caracteres")
    @Column(name = "responsavel_telefone", length = 20)
    private String responsavelTelefone;

}
