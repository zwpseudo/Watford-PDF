package watford.software.SPDF.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import watford.software.SPDF.model.PersistentLogin;

public interface PersistentLoginRepository extends JpaRepository<PersistentLogin, String> {}
