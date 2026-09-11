package com.coachly.adminpanel.client;

import com.coachly.adminpanel.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(nullable = false)
    @Builder.Default
    private String locale = "en";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

}
