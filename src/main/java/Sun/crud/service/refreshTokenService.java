package Sun.crud.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Sun.crud.entity.RefreshToken;
import Sun.crud.repository.refreshTokenRepository;

@Service
public class refreshTokenService {
	@Autowired
	private refreshTokenRepository refreshTokenRepository;

	public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
    @Transactional(readOnly = true)
	public Optional<RefreshToken> findRefreshToken(String tokenWithoutBearer) {
	    return refreshTokenRepository.findByValue(tokenWithoutBearer);

		}

}
