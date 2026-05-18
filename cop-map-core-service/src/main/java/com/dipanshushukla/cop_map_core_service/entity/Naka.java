package com.dipanshushukla.cop_map_core_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nakas", indexes = {
        @Index(name = "idx_naka_thana_id", columnList = "thanaId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Naka {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String thanaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String laneDirection;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}