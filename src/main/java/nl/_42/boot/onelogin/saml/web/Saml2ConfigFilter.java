package nl._42.boot.onelogin.saml.web;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Saml2ConfigFilter extends GenericFilterBean {

    private final Saml2Properties properties;
    private final Saml2LoginUrlResolver urlResolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Saml2ConfigFilter(Saml2Properties properties) {
        this.properties = properties;
        this.urlResolver = new Saml2LoginUrlResolver(properties);
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("registrations", getRegistrations());
        body.put("loginUrl", urlResolver.getLoginUrl(request));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().append(objectMapper.writeValueAsString(body));
    }

    private List<Config> getRegistrations() {
        return properties.getRegistrations().values().stream()
            .filter(Registration::isEnabled)
            .map(this::build)
            .collect(Collectors.toList());
    }

    private Config build(Registration registration) {
        return new Config(
            registration.getId(),
            registration.getLabel(),
            StringUtils.isNotBlank(registration.getSignOnUrl()),
            StringUtils.isNotBlank(registration.getLogoutUrl())
        );
    }

    @Getter
    @AllArgsConstructor
    static class Config {

        private String id;
        private String label;
        private boolean enabled;
        private boolean logout;

    }

}
