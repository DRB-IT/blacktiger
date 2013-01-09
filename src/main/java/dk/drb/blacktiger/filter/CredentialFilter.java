/*
 * Copyright by Apaq 2011-2013
 */
package dk.drb.blacktiger.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author michael
 */
public class CredentialFilter implements Filter {

    private class RequestWrapper extends HttpServletRequestWrapper {

        private String username;
        private String password;

        public RequestWrapper(String username, String password, HttpServletRequest request) {
            super(request);
            this.username = username;
            this.password = password;
        }

        @Override
        public String getHeader(String string) {
            if (string.equals("Authorization")) {
                return "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes());
            }
            return super.getHeader(string);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String user = req.getParameter("p");
        String pass = req.getParameter("k");

        if (user != null && pass != null) {
            req = new RequestWrapper(user, pass, req);
        }
        
        chain.doFilter(req, response);

    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
    }
}
