package nl._42.boot.onelogin.saml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class Saml2OneLoginAutoConfigurationTest extends AbstractSpringBootTest {

	@Autowired
	private Saml2Properties properties;

	@Test
	void configuration_shouldSucceed() {
		Registration registration = properties.getRegistration("saml-service");
		Assertions.assertEquals("https://myservice/saml2/login/saml-service", registration.getServiceProviderId());
		Assertions.assertEquals("https://saml-service/sso", registration.getIdentityProviderId());
	}

}
