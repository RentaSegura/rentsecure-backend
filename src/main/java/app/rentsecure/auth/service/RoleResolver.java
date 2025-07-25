package app.rentsecure.auth.service;

import java.util.List;

public interface RoleResolver {
    List<String> resolveRoles(Long userId);
}