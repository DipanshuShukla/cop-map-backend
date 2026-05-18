package com.dipanshushukla.cop_map_core_service.entity;

import com.dipanshushukla.cop_map_core_service.model.Coordinate;
import com.dipanshushukla.cop_map_core_service.model.OperationStatus;
import com.dipanshushukla.cop_map_core_service.model.OperationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "operations", indexes = {
        @Index(name = "idx_operation_thana_id", columnList = "thanaId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String thanaId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // This can store the ID of a Naka or a Beat, depending on the OperationType
    private String referenceId;

    // Used exclusively for ad-hoc Bandobast polygons
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Coordinate> customGeofence;

    // Cascade operations so saving an Operation with Assignments saves everything
    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}