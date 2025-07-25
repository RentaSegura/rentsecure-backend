package app.rentsecure.contract.entity;

import app.rentsecure.property.entity.Property;
import app.rentsecure.userprofile.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "contracts",
       uniqueConstraints = {
         @UniqueConstraint(
           name = "uq_contract_active_per_property",
           columnNames = {"property_id"}
         ) /* ⇢ se activa vía partial index en la migración */
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contract {

    public enum Status { PENDING_SIGNATURE, ACTIVE, FINISHED, CANCELLED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_profile_id", nullable = false)
    private UserProfile tenantProfile;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "monthly_amount", nullable = false, scale = 2)
    private BigDecimal monthlyAmount;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(name = "pdf_sha256_hash", length = 64)
    private String pdfSha256Hash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.PENDING_SIGNATURE;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }
}
