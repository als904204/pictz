package online.pictz.api.user.repository;

import java.util.Optional;
import online.pictz.api.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByProviderId(String providerId);
}
