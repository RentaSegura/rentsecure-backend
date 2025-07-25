package app.rentsecure.userprofile.entity;

import app.rentsecure.auth.entity.User;
import app.rentsecure.property.entity.Property;
import app.rentsecure.contract.entity.Contract;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long Id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "updated_at")
    private Instant updatedAt;

    /* ---------- Relaciones inversas útiles ---------- */

    @OneToMany(mappedBy = "ownerProfile", fetch = FetchType.LAZY)
    private List<Property> properties;

    @OneToMany(mappedBy = "tenantProfile", fetch = FetchType.LAZY)
    private List<Contract> contractsAsTenant;

    @PrePersist @PreUpdate
    void touchTimestamp() { this.updatedAt = Instant.now(); }
}
