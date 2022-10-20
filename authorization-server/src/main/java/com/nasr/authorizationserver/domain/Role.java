package com.nasr.authorizationserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.nasr.authorizationserver.domain.Role.NAME;
import static com.nasr.authorizationserver.domain.Role.TABLE_NAME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TABLE_NAME,uniqueConstraints = @UniqueConstraint(name = "define_unique",columnNames = NAME))
@Entity
public class Role {

    public static final String TABLE_NAME="role_table";
    public static final String NAME="name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = NAME)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}
