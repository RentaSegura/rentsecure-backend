package app.rentsecure.property.repository;
import app.rentsecure.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    boolean existsByOwnerProfileId(Long ownerProfileId);
}