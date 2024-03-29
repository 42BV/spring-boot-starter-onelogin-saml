/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package nl._42.boot.onelogin.saml;

import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.onelogin.saml2.util.Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * SAML properties.
 *
 * @author Jeroen van Schagen
 * @since Oct 30, 2014
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "onelogin.saml")
public class Saml2Properties implements InitializingBean {

    private static final String PROPERTY_PREFIX = "onelogin.saml2.";

    private boolean enabled;

    private String baseUrl;

    private String defaultLoginUrl;
    private boolean defaultLoginSkipRedirect;

    private String successUrl = "/";
    private String failureUrl = "/error";

    /**
     * Session timeout.
     */
    private int sessionTimeout = 21600;

    /**
     * Custom properties.
     */
    private Properties properties = new Properties();

    /**
     * Registered providers.
     */
    private Map<String, Registration> registrations = new HashMap<>();

    /**
     * Retrieve the registration.
     * @param registrationId the registration ID
     * @return the properties
     */
    public Registration getRegistration(String registrationId) {
        Registration registration = registrations.get(registrationId);
        if (registration == null) {
            throw new Saml2Exception("Unknown Registration " + registrationId);
        }
        return registration;
    }

    /**
     * Build the SAML2 settings for a registration.
     * @param registration the registration properties
     * @return the settings, must exist in config
     */
    public Saml2Settings getSettings(Registration registration) throws CertificateException {
        SettingsBuilder builder = new SettingsBuilder();
        Map<String, Object> values = new HashMap<>();

        // Service provider properties
        values.put(SettingsBuilder.SP_ENTITYID_PROPERTY_KEY, registration.getServiceProviderId());
        values.put(SettingsBuilder.SP_X509CERT_PROPERTY_KEY, getCertificate(registration.getServiceProviderCertificate()));
        values.put(SettingsBuilder.SP_ASSERTION_CONSUMER_SERVICE_URL_PROPERTY_KEY, getSignOnUrl(registration));
        values.put(SettingsBuilder.SP_ASSERTION_CONSUMER_SERVICE_BINDING_PROPERTY_KEY, registration.getSignOnBinding());
        values.put(SettingsBuilder.SP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, getSingleLogoutUrl(registration));
        values.put(SettingsBuilder.SP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY, registration.getLogoutBinding());

        // Identity provider properties
        values.put(SettingsBuilder.IDP_ENTITYID_PROPERTY_KEY, registration.getIdentityProviderId());
        values.put(SettingsBuilder.IDP_X509CERT_PROPERTY_KEY, getCertificate(registration.getCertificate()));
        values.put(SettingsBuilder.IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY, registration.getSignOnUrl());
        values.put(SettingsBuilder.IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY, registration.getLogoutUrl());

        values.put(SettingsBuilder.SECURITY_SIGNATURE_ALGORITHM, registration.getSignatureAlgorithm());

        builder.fromProperties(build(properties));
        builder.fromProperties(build(registration.getProperties()));
        builder.fromValues(values);

        Saml2Settings settings = builder.build();
        settings.setSPValidationOnly(!registration.isValidate());
        return settings;
    }

    public String getSignOnUrl(Registration registration) {
        return getUrl("SSO", registration);
    }

    public String getSingleLogoutUrl(Registration registration) {
        return getUrl("SingleLogout", registration);
    }

    public String getLogoutUrl(Registration registration) {
        return getUrl("logout", registration);
    }

    private String getUrl(String path, Registration registration) {
        return String.format("%s/saml2/%s/%s", baseUrl, path, registration.getId());
    }

    private Properties build(Properties properties) {
        Properties mapped = new Properties();
        for (String name : properties.stringPropertyNames()) {
            mapped.put(PROPERTY_PREFIX + name, properties.getProperty(name));
        }
        return mapped;
    }

    private X509Certificate getCertificate(String content) throws CertificateException {
        if (StringUtils.isBlank(content)) {
            return null;
        }

        return Util.loadCert(content);
    }

    @Override
    public void afterPropertiesSet() {
        registrations.forEach((id, registration) ->
            configure(registration, id)
        );
    }

    private void configure(Registration registration, String id) {
        registration.setId(id);

        if (StringUtils.isBlank(registration.getServiceProviderId())) {
            registration.setServiceProviderId(getLoginUrl(baseUrl, id));
        }
    }

    private static String getLoginUrl(String baseUrl, String id) {
        String basePath = StringUtils.removeEnd(baseUrl, "/");
        return String.format("%s/saml2/login/%s", basePath, id);
    }

    /**
     * Refresh properties after context change, done programmatically
     * because not every library user has cloud libraries available.
     * @param properties the refreshed properties
     */
    public void refresh(Saml2Properties properties) {
        this.enabled = properties.isEnabled();
        this.baseUrl = properties.getBaseUrl();
        this.defaultLoginUrl = properties.getDefaultLoginUrl();
        this.defaultLoginSkipRedirect = properties.isDefaultLoginSkipRedirect();
        this.successUrl = properties.getSuccessUrl();
        this.failureUrl = properties.getFailureUrl();
        this.sessionTimeout = properties.getSessionTimeout();
        this.properties = new Properties(properties.getProperties());
        this.registrations = new HashMap<>(properties.getRegistrations());

        afterPropertiesSet();
    }

}
