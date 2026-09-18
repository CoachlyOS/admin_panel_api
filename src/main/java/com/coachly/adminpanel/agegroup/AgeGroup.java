package com.coachly.adminpanel.agegroup;

import com.coachly.adminpanel.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "age_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgeGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private String slug;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private Map<String, String> name = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
