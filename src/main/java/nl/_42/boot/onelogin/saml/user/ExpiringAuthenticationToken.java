package nl._42.boot.onelogin.saml.user;

import java.time.LocalDateTime;

public interface ExpiringAuthenticationToken {

    LocalDateTime getExpiration();

}
