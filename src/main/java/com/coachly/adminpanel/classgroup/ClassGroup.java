package com.coachly.adminpanel.classgroup;

import com.coachly.adminpanel.agegroup.AgeGroup;
import com.coachly.adminpanel.common.BaseEntity;
import com.coachly.adminpanel.discipline.Discipline;
import com.coachly.adminpanel.level.Level;
import com.coachly.adminpanel.professional.Professional;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "class_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "age_group_id", nullable = false)
    private AgeGroup ageGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_trainer_id", nullable = false)
    private Professional defaultTrainer;

    @Column(nullable = false)
    private Integer capacity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private Map<String, String> name = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
