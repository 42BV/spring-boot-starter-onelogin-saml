package nl._42.boot.onelogin.saml.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Saml2LogoutHandler {

    private final SecurityContextRepository securityContextRepository;

    private List<Saml2LogoutListener> listeners = new ArrayList<>();

    public Saml2LogoutHandler(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    /**
     * This callback is invoked after logging out with success.
     * @param registration the registration
     * @param request the request
     * @param response the response
     */
    public void onLogoutSuccess(Registration registration, HttpServletRequest request, HttpServletResponse response) {
        log.info("Logout successful");

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        securityContextRepository.saveContext(context, request, response);

        listeners.forEach(listener -> listener.onLogoutSuccess(registration));
    }

    /**
     * Configure the logout listeners.
     * @param listeners the listeners
     */
    @Autowired(required = false)
    public void setListeners(List<Saml2LogoutListener> listeners) {
        this.listeners = listeners;
    }

}
