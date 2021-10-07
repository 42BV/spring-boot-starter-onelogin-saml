package nl._42.boot.onelogin.saml.user;

import com.onelogin.saml2.Auth;
import lombok.AllArgsConstructor;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Response;
import org.joda.time.DateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@AllArgsConstructor
public class Saml2AuthenticationProvider {

    private final Saml2UserService userService;

    public Authentication authenticate(Auth auth, Registration registration) throws AuthenticationException {
        Saml2Response response = new Saml2Response(auth, registration);
        UserDetails details = userService.load(response);
        LocalDateTime expiration = convert(auth.getSessionExpiration());

        return new Saml2Authentication(details, response, expiration);
    }

    private LocalDateTime convert(DateTime date) {
        if (date == null) {
            return null;
        }

        return LocalDateTime.of(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth(),
            date.getHourOfDay(),
            date.getMinuteOfHour(),
            date.getSecondOfMinute()
        );
    }

}
