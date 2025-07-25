package app.rentsecure.contract.repository;
import app.rentsecure.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByTenantProfileId(Long tenantProfileId);
}