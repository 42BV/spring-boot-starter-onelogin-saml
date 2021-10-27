package nl._42.boot.onelogin.saml;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl._42.boot.onelogin.saml.user.Saml2AuthenticationProvider;
import nl._42.boot.onelogin.saml.user.Saml2UserService;
import nl._42.boot.onelogin.saml.web.Saml2ConfigFilter;
import nl._42.boot.onelogin.saml.web.Saml2FailureHandler;
import nl._42.boot.onelogin.saml.web.Saml2Filter;
import nl._42.boot.onelogin.saml.web.Saml2LoginFilter;
import nl._42.boot.onelogin.saml.web.Saml2LoginProcessingFilter;
import nl._42.boot.onelogin.saml.web.Saml2LogoutFilter;
import nl._42.boot.onelogin.saml.web.Saml2LogoutProcessingFilter;
import nl._42.boot.onelogin.saml.web.Saml2MetadataDisplayFilter;
import nl._42.boot.onelogin.saml.web.Saml2SuccessRedirectHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.RememberMeServices;

import javax.servlet.Filter;
import java.util.Optional;

/**
 * Enable SAML configuration.
 */
@Slf4j
@Configuration
@ComponentScan(basePackageClasses = Saml2Properties.class)
public class Saml2OneLoginAutoConfiguration {

    static final String CONFIG_URL     = "/saml2/config";
    static final String METADATA_URL   = "/saml2/metadata/**";
    static final String LOGIN_URL      = "/saml2/login/**";
    static final String LOGOUT_URL     = "/saml2/logout/**";
    static final String SSO_URL        = "/saml2/SSO/**";
    static final String SLO_URL        = "/saml2/SingleLogout/**";

    @Bean("oneLoginSaml2Properties")
    public Saml2Properties oneLoginSaml2Properties() {
        return new Saml2Properties();
    }

    @Configuration
    @AllArgsConstructor
    @ConditionalOnProperty(name = "onelogin.saml.enabled", havingValue = "true")
    public static class Saml2OneLoginConfiguration {

        private final Saml2Properties properties;

        @Lazy
        @Autowired
        private Optional<RememberMeServices> rememberMeServices;

        // Web filters

        @Bean
        public Saml2Filter saml2Filter() {
            Saml2Filter chain = new Saml2Filter();
            chain.on(CONFIG_URL, new Saml2ConfigFilter(properties));
            chain.on(METADATA_URL, new Saml2MetadataDisplayFilter(properties));

            // Redirect to IDP login page and handle SSO
            Filter loginFilter = chain.on(LOGIN_URL, new Saml2LoginFilter(properties));
            chain.on(SSO_URL, HttpMethod.POST, saml2LoginProcessingFilter());
            chain.on(SSO_URL, loginFilter);

            // Redirect to IDP logout page and handle SLO
            chain.on(LOGOUT_URL, new Saml2LogoutFilter(properties));
            chain.on(SLO_URL, new Saml2LogoutProcessingFilter(properties));
            return chain;
        }

        private Saml2LoginProcessingFilter saml2LoginProcessingFilter() {
            return new Saml2LoginProcessingFilter(
                properties,
                oneLoginSaml2AuthenticationProvider(),
                oneLoginSaml2SuccessRedirectHandler(),
                oneLoginSaml2FailureHandler()
            );
        }

        @Bean
        public Saml2AuthenticationProvider oneLoginSaml2AuthenticationProvider() {
            return new Saml2AuthenticationProvider(oneLoginSaml2UserService());
        }

        @Bean
        public Saml2UserService oneLoginSaml2UserService() {
            return new Saml2UserService(properties);
        }

        @Bean
        public Saml2SuccessRedirectHandler oneLoginSaml2SuccessRedirectHandler() {
            return new Saml2SuccessRedirectHandler(
                rememberMeServices.orElse(null),
                properties
            );
        }

        @Bean
        public Saml2FailureHandler oneLoginSaml2FailureHandler() {
            return new Saml2FailureHandler(properties);
        }

    }

}
