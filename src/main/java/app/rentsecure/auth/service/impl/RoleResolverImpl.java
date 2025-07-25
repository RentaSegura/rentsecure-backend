package app.rentsecure.auth.service.impl;
import app.rentsecure.property.repository.PropertyRepository;
import app.rentsecure.userprofile.repository.UserProfileRepository;
import app.rentsecure.contract.repository.ContractRepository;
import app.rentsecure.auth.service.RoleResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleResolverImpl implements RoleResolver {

    private final UserProfileRepository profileRepo;
    private final PropertyRepository propertyRepo;
    private final ContractRepository contractRepo;

    @Override
    public List<String> resolveRoles(Long userId) {
        List<String> roles = new ArrayList<>();
        roles.add("USER");                        // Rol base para todos

        profileRepo.findByUserId(userId).ifPresent(profile -> {
            Long profileId = profile.getId();
            if (propertyRepo.existsByOwnerProfileId(profileId)) roles.add("LANDLORD");
            if (contractRepo.existsByTenantProfileId(profileId)) roles.add("TENANT");
        });
        return roles;
    }
}