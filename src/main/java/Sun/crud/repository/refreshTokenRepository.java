package Sun.crud.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Sun.crud.entity.RefreshToken;


public interface refreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value);

}
