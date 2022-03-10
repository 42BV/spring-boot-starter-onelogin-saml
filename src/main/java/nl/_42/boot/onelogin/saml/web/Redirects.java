package nl._42.boot.onelogin.saml.web;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

final class Redirects {

    private static final String LOCATION = "Location";

    static void redirectTo(HttpServletResponse response, String location) {
        response.setHeader(LOCATION, location);
        response.setStatus(HttpStatus.SEE_OTHER.value());
    }

}
