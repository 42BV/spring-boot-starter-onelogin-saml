package nl._42.boot.onelogin.saml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;

class Saml2OneLoginAutoConfigurationTest extends AbstractSpringBootTest {

	@Autowired
	private Saml2Properties properties;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ApplicationContext context;

	@Test
	void configuration_shouldSucceed() {
		Registration registration = properties.getRegistration("saml-service");
		Assertions.assertEquals("https://myservice/saml2/login/saml-service", registration.getServiceProviderId());
		Assertions.assertEquals("https://saml-service/sso", registration.getIdentityProviderId());
	}

	@Test
	void refresh_shouldSucceed() {
		publisher.publishEvent(new RefreshEvent(context, this, "Test"));

		Registration registration = properties.getRegistration("saml-service");
		Assertions.assertEquals("https://myservice/saml2/login/saml-service", registration.getServiceProviderId());
	}

}
