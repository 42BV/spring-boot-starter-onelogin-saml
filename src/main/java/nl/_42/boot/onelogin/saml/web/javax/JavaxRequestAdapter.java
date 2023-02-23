package nl._42.boot.onelogin.saml.web.javax;

import lombok.AllArgsConstructor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

@AllArgsConstructor
public class JavaxRequestAdapter implements javax.servlet.http.HttpServletRequest {

    private final jakarta.servlet.http.HttpServletRequest request;

    @Override public String getAuthType() {
        return request.getAuthType();
    }

    @Override public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override public long getDateHeader(String s) {
        return request.getDateHeader(s);
    }

    @Override public String getHeader(String s) {
        return request.getHeader(s);
    }

    @Override public Enumeration getHeaders(String s) {
        return request.getHeaders(s);
    }

    @Override public Enumeration getHeaderNames() {
        return request.getHeaderNames();
    }

    @Override public int getIntHeader(String s) {
        return request.getIntHeader(s);
    }

    @Override public String getMethod() {
        return request.getMethod();
    }

    @Override public String getPathInfo() {
        return request.getPathInfo();
    }

    @Override public String getPathTranslated() {
        return request.getPathTranslated();
    }

    @Override public String getContextPath() {
        return request.getContextPath();
    }

    @Override public String getQueryString() {
        return request.getQueryString();
    }

    @Override public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override public boolean isUserInRole(String s) {
        return request.isUserInRole(s);
    }

    @Override public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @Override public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    @Override public String getServletPath() {
        return request.getServletPath();
    }

    @Override public HttpSession getSession(boolean b) {
        return new JavaxSessionAdapter(request.getSession(b));
    }

    @Override public HttpSession getSession() {
        return new JavaxSessionAdapter(request.getSession());
    }

    @Override public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    @Override public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    @Override public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override public Object getAttribute(String s) {
        return request.getAttribute(s);
    }

    @Override public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }

    @Override public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        request.setCharacterEncoding(s);
    }

    @Override public int getContentLength() {
        return request.getContentLength();
    }

    @Override public String getContentType() {
        return request.getContentType();
    }

    @Override public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override public String getParameter(String s) {
        return request.getParameter(s);
    }

    @Override public Enumeration getParameterNames() {
        return request.getParameterNames();
    }

    @Override public String[] getParameterValues(String s) {
        return request.getParameterValues(s);
    }

    @Override public Map getParameterMap() {
        return request.getParameterMap();
    }

    @Override public String getProtocol() {
        return request.getProtocol();
    }

    @Override public String getScheme() {
        return request.getScheme();
    }

    @Override public String getServerName() {
        return request.getServerName();
    }

    @Override public int getServerPort() {
        return request.getServerPort();
    }

    @Override public BufferedReader getReader() throws IOException {
        return request.getReader();
    }

    @Override public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override public void setAttribute(String s, Object o) {
        request.setAttribute(s, o);
    }

    @Override public void removeAttribute(String s) {
        request.removeAttribute(s);
    }

    @Override public Locale getLocale() {
        return request.getLocale();
    }

    @Override public Enumeration getLocales() {
        return request.getLocales();
    }

    @Override public boolean isSecure() {
        return request.isSecure();
    }

    @Override public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override public String getRealPath(String s) {
        return null;
    }

    @Override public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override public String getLocalName() {
        return request.getLocalName();
    }

    @Override public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override public int getLocalPort() {
        return request.getLocalPort();
    }

}
