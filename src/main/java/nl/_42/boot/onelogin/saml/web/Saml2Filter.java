package nl._42.boot.onelogin.saml.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Saml2Filter extends GenericFilterBean {

    private static final String WILDCARD = "/**";

    private final List<SecurityFilterChain> filters = new ArrayList<>();

    public void on(String url, Filter filter) {
        String pattern = url.endsWith(WILDCARD) ? url : url + WILDCARD;
        PathPatternRequestMatcher matcher = PathPatternRequestMatcher.pathPattern(pattern);
        filters.add(new DefaultSecurityFilterChain(matcher, filter));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Filter filter = getFilter(httpServletRequest);

        if (filter == null) {
            chain.doFilter(request, response);
        } else {
            proceed(request, response, filter, chain);
        }
    }

    private void proceed(ServletRequest request, ServletResponse response, Filter filter, FilterChain chain) throws IOException, ServletException {
        filter.doFilter(request, response, chain);
    }

    private Filter getFilter(HttpServletRequest request) {
        return filters.stream()
            .filter(filter -> filter.matches(request))
            .flatMap(chain -> chain.getFilters().stream())
            .findFirst()
            .orElse(null);
    }

}
