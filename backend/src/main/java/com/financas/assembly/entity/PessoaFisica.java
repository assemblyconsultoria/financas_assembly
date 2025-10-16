package com.financas.assembly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity representing an individual client (pessoa física).
 * Extends Cliente with specific fields for individuals like CPF and birth date.
 */
@Entity
@DiscriminatorValue("PF")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFisica extends Cliente {

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Pattern(regexp = "\\d{9}", message = "RG deve conter 9 dígitos")
    @Column(name = "rg", length = 9)
    private String rg;

    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Size(max = 50, message = "Profissão deve ter no máximo 50 caracteres")
    @Column(name = "profissao", length = 50)
    private String profissao;

    @Size(max = 20, message = "Estado civil deve ter no máximo 20 caracteres")
    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

}
