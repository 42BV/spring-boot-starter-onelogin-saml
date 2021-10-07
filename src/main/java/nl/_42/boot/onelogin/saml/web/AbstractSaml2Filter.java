package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.settings.Saml2Settings;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            Registration idp = getIdp(httpServletRequest);
            Saml2Settings settings = properties.build(idp);
            Auth auth = new Auth(settings, httpServletRequest, httpServletResponse);
            doFilter(auth, idp, httpServletRequest, httpServletResponse, chain);
        } catch (SAMLException | CertificateException se) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("Could perform authentication due to an unexpected error", se);
        }
    }

    private Registration getIdp(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String name = StringUtils.substringAfterLast(uri, "/");
        return properties.getIdp(name);
    }

    protected abstract void doFilter(Auth auth, Registration idp, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException, SAMLException;

}
