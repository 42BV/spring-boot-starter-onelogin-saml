package nl._42.boot.onelogin.saml.web;

import com.onelogin.saml2.exception.SAMLException;
import com.onelogin.saml2.settings.Saml2Settings;
import nl._42.boot.onelogin.saml.Registration;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;

public class Saml2MetadataDisplayFilter extends AbstractSaml2Filter {

    private static final String SPRING_SAML_METADATA = "spring_saml_metadata";

    private static final String XML = ".xml";
    private static final String SEPARATOR = "-";
    private static final String PROTOCOL = "://";
    private static final String PATH = "/";

    public Saml2MetadataDisplayFilter(Saml2Properties properties) {
        super(properties);
    }

    static String getMetadataFileName(String provider) {
        String name = getName(provider);
        return name + XML;
    }

    private static String getName(String provider) {
        if (StringUtils.isBlank(provider)) {
            return SPRING_SAML_METADATA;
        }

        String name = provider;
        if (name.contains(PROTOCOL)) {
            name = StringUtils.substringAfter(provider, PROTOCOL);
        }
        if (name.contains(PATH)) {
            name = StringUtils.substringBefore(name, PATH);
        }
        return name.replaceAll("\\.", SEPARATOR).replaceAll("/", SEPARATOR);
    }

    @Override
    protected void doFilter(Saml2Settings settings, Registration registration, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws SAMLException, IOException {
        String fileName = getMetadataFileName(registration.getServiceProviderId());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        try {
            String metadata = settings.getSPMetadata();
            response.getWriter().append(metadata);
        } catch (CertificateEncodingException e) {
            throw new SAMLException("Could not retrieve metadata", e);
        }
    }

}
