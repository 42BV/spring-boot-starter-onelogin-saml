package nl._42.boot.onelogin.saml.web;

import nl._42.boot.onelogin.saml.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class Saml2LogoutHandler {

    private List<Saml2LogoutListener> listeners = new ArrayList<>();

    /**
     * This callback is invoked after logging out with success.
     * @param registration the registration
     */
    public void onLogoutSuccess(Registration registration) {
        listeners.forEach(listener -> listener.onLogoutSuccess(registration));

        SecurityContextHolder.clearContext();
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
