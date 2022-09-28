# spring-boot-starter-onelogin-saml

*Discription here*

## Configuration

Include the dependency in your Spring Boot application and add the configurations to your application.yml:

```yaml
onelogin:
  saml:
    enabled: true
    base-url: https://serviceProvider.com
    success-url: /#/dashboard
    failure-url: /#/error
    registrations:
      IdPName:
        service_provider_id: https://sp.com/saml2/login/IdPName
        identity_provider_id: https://IdP.com/o/saml2?idpid=<ID>
        metadata_url: https://IdP.com/metadata
        sign_on_url: https://IdP.com/o/saml2?idpid=<ID>
        certificate: '
                -----BEGIN CERTIFICATE-----
                { certificate }
                -----END CERTIFICATE-----
              '
```

Create a SamlMapper in your application to authenticate the user:

(You can also decorate the user here if you want)
```JAVA
@Component
@AllArgsConstructor
class SamlMapper implements Saml2UserDecorator {

    @Override
    public UserDetails decorate(UserDetails details, Saml2Response response) {
        return details;
    }
}
```

Make sure anyone is able to login in your SecurityConfig.

```JAVA
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/saml2/**").permitAll()
                .anyRequest().authenticated();
    }

}
```

## Use

To go to the IdP login the url path is as followed = `https://host/saml2/login/identityProviderName`
