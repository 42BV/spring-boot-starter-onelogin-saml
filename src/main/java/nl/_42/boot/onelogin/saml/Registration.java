package nl._42.boot.onelogin.saml;

import lombok.Getter;
import lombok.Setter;

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

}
