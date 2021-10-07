/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.boot.onelogin.saml.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@AllArgsConstructor
public class Saml2FailureHandler implements AuthenticationFailureHandler {

    private final Saml2Properties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        redirectTo(response, properties.getFailureUrl());
    }
    
    private void redirectTo(HttpServletResponse response, String location) {
        response.setHeader("Location", location);
        response.setStatus(HttpStatus.SEE_OTHER.value());
    }

}
