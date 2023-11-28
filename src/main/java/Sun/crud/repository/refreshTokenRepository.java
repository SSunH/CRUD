package Sun.crud.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import Sun.crud.entity.RefreshToken;


public interface refreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	RefreshToken save(RefreshToken refreshToken);

}
