package Sun.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Sun.crud.entity.SignupEntity;

public interface UserRepository extends JpaRepository<SignupEntity, Long> {

	SignupEntity findById(String username);

}
