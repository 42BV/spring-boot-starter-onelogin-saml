package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.settings.Saml2Settings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Exception;
import nl._42.boot.onelogin.saml.Saml2Properties;
import nl._42.boot.onelogin.saml.user.Saml2AuthenticationProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

@Slf4j
public class Saml2LoginProcessingFilter extends AbstractSaml2Filter {

    private final Saml2AuthenticationProvider authenticationProvider;
    private final Saml2SuccessHandler successHandler;
    private final Saml2FailureHandler failureHandler;

    private final SecurityContextRepository securityContextRepository;

    public Saml2LoginProcessingFilter(
        Saml2Properties properties,
        Saml2AuthenticationProvider authenticationProvider,
        Saml2SuccessHandler successHandler,
        Saml2FailureHandler failureHandler,
        SecurityContextRepository securityContextRepository
    ) {
        super(properties);

        this.authenticationProvider = authenticationProvider;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException, SAMLException {
        try {
            Auth auth = getAuth(settings, request, response);
            auth.processResponse();

            if (auth.isAuthenticated()) {
                onAuthenticated(auth, registration, request, response);
            } else {
                onFailure(auth, request, response);
            }
        } catch (Exception cause) {
            failureHandler.onFailure(request, response, new Saml2Exception("Could not process SAML2 response", cause));
        }
    }

    private void onAuthenticated(Auth auth, Registration registration, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            Authentication authentication = authenticationProvider.authenticate(auth, registration);

            log.info("Login '{}' successful", authentication.getName());
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            securityContextRepository.saveContext(context, request, response);

            if (registration.isDebug()) {
                auth.getAttributes().forEach((name, values) ->
                    log.debug("SAML Attribute '{}' = {}", name, StringUtils.join(values, ", "))
                );
            }

            successHandler.onSuccess(request, response, authentication);
        } catch (AuthenticationException exception) {
            failureHandler.onFailure(request, response, exception);
        }
    }

    private void onFailure(Auth auth, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String message = String.format("Could not authenticate: (%s) %s", auth.getLastErrorReason(), String.join(", ", auth.getErrors()));
        failureHandler.onFailure(request, response, new Saml2Exception(message));
    }

}
