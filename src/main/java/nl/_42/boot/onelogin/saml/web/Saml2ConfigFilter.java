package nl._42.boot.onelogin.saml.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Saml2ConfigFilter extends GenericFilterBean {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Saml2Properties properties;

    public Saml2ConfigFilter(Saml2Properties properties) {
        this.properties = properties;
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        List<Config> registrations = properties.getRegistrations().values().stream().map(this::build).collect(Collectors.toList());

        Map<String, Object> body = new HashMap<>();
        body.put("registrations", registrations);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().append(objectMapper.writeValueAsString(body));
    }

    private Config build(Registration registration) {
        return new Config(
            registration.getId(),
            StringUtils.isNotBlank(registration.getSignOnUrl()),
            StringUtils.isNotBlank(registration.getLogoutUrl())
        );
    }

    @Getter
    @AllArgsConstructor
    static class Config {

        private String id;
        private boolean enabled;
        private boolean logout;

    }

}
