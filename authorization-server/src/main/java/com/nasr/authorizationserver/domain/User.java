package com.nasr.authorizationserver.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.nasr.authorizationserver.domain.User.EMAIL;
import static com.nasr.authorizationserver.domain.User.TABLE_NAME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_NAME,uniqueConstraints = @UniqueConstraint(name = "define_unique",columnNames = EMAIL))

public class User {

    public static final String TABLE_NAME="user_table";
    private static final String FIRST_NAME="first_name";
    private static final String LAST_NAME="last_name";
    public static final String EMAIL="email";
    private static final String PASSWORD="password";
    private static final String ROLE_ID="role_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = FIRST_NAME,nullable = false)
    private String firstName;

    @Column(name = LAST_NAME,nullable = false)
    private String lastName;

    @Column(name = EMAIL)
    private String email;

    @Column(name = PASSWORD ,nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ROLE_ID)
    private Role role;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

    }
}
