package nl._42.boot.onelogin.saml.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Saml2Filter extends GenericFilterBean {

    private final List<SecurityFilterChain> filters = new ArrayList<>();

    public Filter on(String url, Filter filter) {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
        return on(matcher, filter);
    }

    public Filter on(String url, HttpMethod method, Filter filter) {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(url, method.name());
        return on(matcher, filter);
    }

    public Filter on(RequestMatcher matcher, Filter filter) {
        filters.add(new DefaultSecurityFilterChain(matcher, filter));
        return filter;
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
