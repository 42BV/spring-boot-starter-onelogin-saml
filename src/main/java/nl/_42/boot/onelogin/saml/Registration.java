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

    /**
     * Service Provider identifier
     */
    private String serviceProviderId;

    /**
     * Identity Provider identifier
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
     * Single Log Out URL
     */
    private String logoutUrl;

    /**
     * IDP certificate, used for verification.
     */
    private String certificate;

    /**
     * Force new authentication upon login.
     */
    private boolean forceAuthN;

    /**
     * Enforces validation.
     */
    private boolean validate = true;

    /**
     * Custom properties that are set.
     */
    private Properties properties = new Properties();

    /**
     * Attribute name translations.
     */
    private Map<String, String> attributes = new HashMap<>();

    /**
     * Retrieve the translated attribute.
     * @param name the name
     * @return the translated value
     */
    public Optional<String> getAttribute(String name) {
        String result = attributes.get(name);
        return Optional.ofNullable(result).filter(StringUtils::isNotBlank);
    }

}
