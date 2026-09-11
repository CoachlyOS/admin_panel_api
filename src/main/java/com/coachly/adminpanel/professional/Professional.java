package com.coachly.adminpanel.professional;

import com.coachly.adminpanel.appointment.Appointment;
import com.coachly.adminpanel.common.BaseEntity;
import com.coachly.adminpanel.discipline.Discipline;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professional extends BaseEntity {

    @Version
    @Column(nullable = false)
    private Integer version;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "avatar_id")
    private String avatarId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, name = "password_hash")
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(nullable = false)
    @Builder.Default
    private String locale = "en";

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> biography = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> socials = new HashMap<>();

    @OneToMany(mappedBy = "professional", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Appointment> appointments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "professional_disciplines",
        joinColumns = @JoinColumn(name = "professional_id"),
        inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    @Builder.Default
    private Set<Discipline> disciplines = new HashSet<>();
}
