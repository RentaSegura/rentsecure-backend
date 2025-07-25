package app.rentsecure.property.entity;

import app.rentsecure.userprofile.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "properties",uniqueConstraints = @UniqueConstraint(columnNames = {"owner_profile_id", "address"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Property {

    public enum Status { DRAFT, ACTIVE, ARCHIVED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long propertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_profile_id", nullable = false)
    private UserProfile ownerProfile;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String type;                       // Ej: "Departamento", "Casa"

    @Column(name = "monthly_price", nullable = false, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "main_image_url", columnDefinition = "TEXT")
    private String mainImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "created_at")
    private Instant createdAt;

    /* ---------- Relaciones ---------- */

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY)
    private List<app.rentsecure.contract.entity.Contract> contracts;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
