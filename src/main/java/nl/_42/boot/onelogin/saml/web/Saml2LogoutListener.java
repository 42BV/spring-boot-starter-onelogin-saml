package nl._42.boot.onelogin.saml.web;

import nl._42.boot.onelogin.saml.Registration;

public interface Saml2LogoutListener {

    void onLogoutSuccess(Registration registration);

}
