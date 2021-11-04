package nl._42.boot.onelogin.saml.web;

import nl._42.boot.onelogin.saml.AbstractSpringBootTest;
import nl._42.boot.onelogin.saml.Saml2Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

public class Saml2ConfigFilterTest extends AbstractSpringBootTest {

    @Autowired
    private Saml2Properties properties;

    @Test
    public void filter_shouldSucceed() throws IOException {
        Saml2ConfigFilter filter = new Saml2ConfigFilter(properties);

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(null, response, null);

        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals("{\"registrations\":[{\"id\":\"saml-service\",\"enabled\":true,\"logout\":false}]}", response.getContentAsString());
    }

}
