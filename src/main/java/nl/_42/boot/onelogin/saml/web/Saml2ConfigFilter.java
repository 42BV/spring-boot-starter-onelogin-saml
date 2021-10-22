package nl._42.boot.onelogin.saml.web;

import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class Saml2ConfigFilter extends GenericFilterBean {

    private final Saml2Properties properties;

    public Saml2ConfigFilter(Saml2Properties properties) {
        this.properties = properties;
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        String registrationIds = properties.getRegistrations().keySet().stream()
            .map(id -> String.format("\"%s\"", id))
            .collect(Collectors.joining(", "));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().append("{\"registrationIds\": [").append(registrationIds).append("]}");
    }

}
