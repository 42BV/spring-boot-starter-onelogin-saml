package nl._42.boot.onelogin.saml.web.javax;

import lombok.AllArgsConstructor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

@AllArgsConstructor
public class JavaxSessionAdapter implements javax.servlet.http.HttpSession {

    private final jakarta.servlet.http.HttpSession session;

    @Override public long getCreationTime() {
        return session.getCreationTime();
    }

    @Override public String getId() {
        return session.getId();
    }

    @Override public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    @Override public ServletContext getServletContext() {
        return null;
    }

    @Override public void setMaxInactiveInterval(int i) {
        session.setMaxInactiveInterval(i);
    }

    @Override public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    @Override public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override public Object getAttribute(String s) {
        return session.getAttribute(s);
    }

    @Override public Object getValue(String s) {
        return null;
    }

    @Override public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }

    @Override public String[] getValueNames() {
        return new String[0];
    }

    @Override public void setAttribute(String s, Object o) {
        session.setAttribute(s, o);
    }

    @Override public void putValue(String s, Object o) {
    }

    @Override public void removeAttribute(String s) {
        session.removeAttribute(s);
    }

    @Override public void removeValue(String s) {
    }

    @Override public void invalidate() {
        session.invalidate();
    }

    @Override public boolean isNew() {
        return session.isNew();
    }

}
