package com.financas.assembly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing system permissions.
 * Used for fine-grained access control.
 */
@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(name = "uk_permission_resource_action", columnNames = {"resource", "action"})
}, indexes = {
    @Index(name = "idx_permission_resource", columnList = "resource"),
    @Index(name = "idx_permission_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da permissão é obrigatório")
    @Size(min = 2, max = 100, message = "Nome da permissão deve ter entre 2 e 100 caracteres")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "Recurso é obrigatório")
    @Size(max = 50, message = "Recurso deve ter no máximo 50 caracteres")
    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @NotBlank(message = "Ação é obrigatória")
    @Size(max = 50, message = "Ação deve ter no máximo 50 caracteres")
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(name = "description")
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    /**
     * Constructor for creating permission with resource and action.
     */
    public Permission(String resource, String action, String description) {
        this.resource = resource;
        this.action = action;
        this.name = resource + "_" + action;
        this.description = description;
    }
}
