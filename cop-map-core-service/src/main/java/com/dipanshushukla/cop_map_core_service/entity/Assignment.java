package com.dipanshushukla.cop_map_core_service.entity;

import com.dipanshushukla.cop_map_core_service.model.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignment_badge", columnList = "badgeNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @Column(nullable = false)
    private String badgeNumber;

    // For Group Bandobasts (e.g., "Platoon Alpha")
    private String teamName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}