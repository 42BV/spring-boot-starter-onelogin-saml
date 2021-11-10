package nl._42.boot.onelogin.saml;

import com.onelogin.saml2.Auth;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Saml2Response {

    private static final String USERNAME = "username";
    private static final String ROLE = "role";

    private final Auth auth;
    private final Registration registration;

    public String getName() {
        return auth.getNameId();
    }

    public Collection<String> getAttributes() {
        List<String> names = auth.getAttributesName();
        return Collections.unmodifiableList(names);
    }

    public Set<String> getValues(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptySet();
        }

        String mapped = registration.getAttribute(name).orElse(name);
        Collection<String> attribute = auth.getAttribute(mapped);
        if (attribute == null) {
            return Collections.emptySet();
        }

        return new HashSet<>(attribute);
    }

    public Optional<String> getValue(String name) {
        return getValues(name)
            .stream()
            .sorted()
            .filter(StringUtils::isNotBlank)
            .findFirst();
    }

    /**
     * Retrieve the username, or throws an error when none can be found
     * @return the username
     */
    public String getUserName() {
        String userName = getValue(USERNAME).orElseGet(this::getName);

        if (StringUtils.isBlank(userName)) {
            throw new Saml2Exception(
                "Missing user name in SAML response, please provide a Name ID or user attribute"
            );
        }

        return userName;
    }

    /**
     * Retrieve the authorities granted to this user.
     * @return the authorities
     */
    public List<GrantedAuthority> getAuthorities() {
        Set<String> roles = getValues(ROLE);

        return roles.stream()
            .map(registration::getAuthority)
            .filter(StringUtils::isNotBlank)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    /**
     * Retrieve the registration identifier (IDP).
     * @return the registration identifier
     */
    public String getRegistrationId() {
        return registration.getId();
    }

}
