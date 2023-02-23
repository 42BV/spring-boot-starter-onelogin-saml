package nl._42.boot.onelogin.saml.web;

import jakarta.servlet.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Component
class Saml2LoginUrlResolver {

    private final Saml2Properties properties;
    private final RestTemplate template;

    Saml2LoginUrlResolver(Saml2Properties properties) {
        this.properties = properties;

        this.template = new RestTemplate();
        template.setErrorHandler(new EmptyErrorHandler());
    }

    public String getLoginUrl(ServletRequest request) {
        if (!properties.isEnabled() || StringUtils.isBlank(properties.getDefaultLoginUrl())) {
            return "";
        }

        String successUrl = request.getParameter("successUrl");
        String loginUrl = getLoginUrl(successUrl);

        if (properties.isDefaultLoginSkipRedirect()) {
            loginUrl = getLocation(loginUrl);
        }

        return loginUrl;
    }

    private String getLoginUrl(String successUrl) {
        UrlBuilder builder = new UrlBuilder(properties.getDefaultLoginUrl());
        if (isNotEmpty(successUrl)) {
            builder.append("?successUrl=").append(successUrl);
        }
        return builder.build();
    }

    private String getLocation(String url) {
        ResponseEntity<String> entity = template.getForEntity(url, String.class);

        HttpStatusCode status = entity.getStatusCode();
        URI location = entity.getHeaders().getLocation();

        if (location != null) {
            url = location.toString();
        } else if (status.is3xxRedirection()) {
            Objects.requireNonNull("SAML login with status " + status.value() + " (redirect) is missing the required 'Location' header");
        } else if (status.isError()) {
            log.warn("Expected HTTP status 3xx (redirect) on login, but received error status {}", status.value());
        } else {
            log.warn("Expected HTTP status 3xx (redirect) on login, but received status {}, please disable 'saml.skip_login_redirect'", status.value());
        }

        return url;
    }

    private class UrlBuilder {

        private StringBuilder url;

        UrlBuilder(String baseUrl) {
            url = new StringBuilder();
            append(baseUrl);
        }

        UrlBuilder path(String path) {
            if (!path.isEmpty() && !path.startsWith("/")) {
                url.append("/");
            }
            return append(path);
        }

        UrlBuilder append(String value) {
            if (value.endsWith("/")) {
                String stripped = value.substring(0, value.length() - 1);
                return append(stripped);
            }

            url.append(value);
            return this;
        }

        String build() {
            return url.toString();
        }

    }

    private static class EmptyErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) {
        }

    }

}
