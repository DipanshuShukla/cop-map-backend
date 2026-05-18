package com.dipanshushukla.cop_map_core_service.entity;

import com.dipanshushukla.cop_map_core_service.model.Coordinate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "beats", indexes = {
        @Index(name = "idx_beat_thana_id", columnList = "thanaId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String thanaId;

    @Column(nullable = false)
    private String name; // e.g., "Sector 4 Night Route"

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Coordinate> routeCoordinates;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}