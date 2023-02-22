package nl._42.boot.onelogin.saml.web;

import nl._42.boot.onelogin.saml.AbstractSpringBootTest;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;

public class Saml2MetadataDisplayFilterTest extends AbstractSpringBootTest {

    @Autowired
    private Saml2Properties properties;

    @Test
    public void filter_shouldSucceed() throws IOException, ServletException {
        Saml2MetadataDisplayFilter filter = new Saml2MetadataDisplayFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("https://myservice/saml2/metadata/saml-service");

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, null);

        Assertions.assertEquals("attachment; filename=\"myservice.xml\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION));

        String metadataXml = response.getContentAsString();
        Assertions.assertTrue(metadataXml.contains("<md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"https://myservice/saml2/SSO/saml-service\" index=\"1\"/>"));
        Assertions.assertTrue(metadataXml.contains("<md:SingleLogoutService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect\" Location=\"https://myservice/saml2/SingleLogout/saml-service\"/>"));
    }

}
