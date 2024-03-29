package watford.software.SPDF.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import watford.software.SPDF.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // Set<Authority> findByUsername(String username);
    Set<Authority> findByUser_Username(String username);
}
