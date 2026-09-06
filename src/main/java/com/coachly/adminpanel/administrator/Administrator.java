package com.coachly.adminpanel.administrator;

import com.coachly.adminpanel.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "administrators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @JdbcTypeCode(SqlTypes.ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "administrator_role")
//    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AdministratorRole role;
}
