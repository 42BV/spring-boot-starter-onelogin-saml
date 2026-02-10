package nl._42.boot.onelogin.saml.web;

import nl._42.boot.onelogin.saml.AbstractSpringBootTest;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

public class Saml2ConfigFilterTest extends AbstractSpringBootTest {

    @Autowired
    private Saml2Properties properties;

    @Test
    public void filter_shouldSucceed() throws IOException {
        Saml2ConfigFilter filter = new Saml2ConfigFilter(properties);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, null);

        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals(
            "{\"registrations\":[{\"enabled\":true,\"id\":\"saml-service\",\"label\":\"SAML Service\",\"logout\":false}],\"loginUrl\":\"https://myservice/saml2/login/saml-service\"}",
            response.getContentAsString()
        );
    }

}
