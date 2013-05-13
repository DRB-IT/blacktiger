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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter for supporting authenticating by supplying username/password via query parameters. <br>
 * <br>
 * This filter will listen for 2 query parameters - 'p' & 'k'.<br>
 * p = username<br>
 * k = password<br>
 * <br>
 * If both 'p' & 'k' is found in a request the wrapped in a way so that the username and password is available
 * as if they were given as basic authentication, that is, as the Authorization header.
 * 
 * 
 */
public class CredentialFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CredentialFilter.class);
    
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
            String value;
            if ("Authorization".equals(string)) {
                value = "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes());
            } else {
                value = super.getHeader(string);
            }
            return value;
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String user = req.getParameter("p");
        String pass = req.getParameter("k");

        if (user != null && pass != null) {
            LOG.debug("User has supplied the secret user/pass query parameters. Wrapping request so they can be used as Basic Auth.");
            req = new RequestWrapper(user, pass, req);
        }
        
        chain.doFilter(req, response);

    }

    public void destroy() { /* EMPTY*/ }

    public void init(FilterConfig filterConfig) { /* EMPTY*/ }
}
