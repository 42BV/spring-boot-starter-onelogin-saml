package nl._42.boot.onelogin.saml;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Getter
@Setter
public class Registration {

    public static final String POST     = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    public static final String REDIRECT = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";

    /**
     * Registration enabled
     */
    private boolean enabled = true;

    /**
     * Registration identifier
     */
    private String id;

    /**
     * Service Provider (SP) identifier
     */
    private String serviceProviderId;

    /**
     * Service Provider certificate
     */
    private String serviceProviderCertificate;

    /**
     * Identity Provider (IDP) identifier
     */
    private String identityProviderId;

    /**
     * Metadata URL
     */
    private String metadataUrl;

    /**
     * Single Sign On URL
     */
    private String signOnUrl;

    /**
     * Single Sign On binding
     */
    private String signOnBinding = POST;

    /**
     * Single Log Out URL
     */
    private String logoutUrl;

    /**
     * Single Log Out binding
     */
    private String logoutBinding = REDIRECT;

    /**
     * IDP certificate
     */
    private String certificate;

    /**
     * Security signature algorithm
     */
    private String signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";

    /**
     * Force new authentication upon login
     */
    private boolean forceAuthN;

    /**
     * Enforces validation
     */
    private boolean validate = true;

    /**
     * Debug attributes on login.
     */
    private boolean debug;

    /**
     * Custom properties that are set
     */
    private Properties properties = new Properties();

    /**
     * Attribute name translations
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Role name translations
     */
    private Map<String, String> roles = new HashMap<>();

    /**
     * Retrieve the translated attribute.
     * @param name the name
     * @return the translated value
     */
    public Optional<String> getAttribute(String name) {
        String result = attributes.get(name);
        return Optional.ofNullable(result).filter(StringUtils::isNotBlank);
    }

    /**
     * Retrieve the authority of a role.
     * @param role the role
     * @return the authority
     */
    public String getAuthority(String role) {
        if (StringUtils.isBlank(role)) {
            return null;
        }

        return roles.get(role);
    }

}
