package Sun.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Sun.crud.entity.SignupEntity;

@Repository
public interface SignupRepository extends JpaRepository<SignupEntity, Long> {

	int countById(String id);

	

}
