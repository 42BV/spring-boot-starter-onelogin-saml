package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import nl._42.boot.onelogin.saml.web.javax.JavaxRequestAdapter;
import nl._42.boot.onelogin.saml.web.javax.JavaxResponseAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.security.cert.CertificateException;

@Slf4j
@AllArgsConstructor
public abstract class AbstractSaml2Filter extends GenericFilterBean {

    private final Saml2Properties properties;

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            Registration registration = getRegistration(httpServletRequest);
            Saml2Settings settings = properties.getSettings(registration);

            doFilter(settings, registration, httpServletRequest, httpServletResponse, chain);
        } catch (SAMLException | CertificateException se) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Could perform authentication due to an unexpected error", se);
        }
    }

    private Registration getRegistration(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String registrationId = StringUtils.substringAfterLast(uri, "/");
        return properties.getRegistration(registrationId);
    }

    protected Auth getAuth(Saml2Settings settings, HttpServletRequest request, HttpServletResponse response) throws SettingsException {
        return new Auth(settings, new JavaxRequestAdapter(request), new JavaxResponseAdapter(response));
    }

    protected abstract void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException, SAMLException;

}
