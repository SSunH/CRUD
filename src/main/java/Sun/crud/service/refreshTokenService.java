package Sun.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Sun.crud.entity.RefreshToken;
import Sun.crud.repository.refreshTokenRepository;

@Service
public class refreshTokenService {
	@Autowired
	private refreshTokenRepository refreshTokenRepository;

	public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

}
