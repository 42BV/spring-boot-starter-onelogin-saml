package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.settings.Saml2Settings;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class Saml2LogoutProcessingFilter extends AbstractSaml2Filter {

    private final String successUrl;

    public Saml2LogoutProcessingFilter(Saml2Properties properties) {
        super(properties);
        this.successUrl = properties.getSuccessUrl();
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        SecurityContextHolder.clearContext();
        log.info("Logout successful");

        response.sendRedirect(successUrl);
    }

}
