/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.boot.onelogin.saml.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class Saml2FailureHandler {

    private final AuthenticationFailureHandler handler;
    private final Saml2Properties properties;

    public void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException, IOException {
        if (handler != null) {
            handler.onAuthenticationFailure(request, response, exception);
        }

        log.error("Error during SAML2 login", exception);
        Redirects.redirectTo(response, properties.getFailureUrl());
    }

}
