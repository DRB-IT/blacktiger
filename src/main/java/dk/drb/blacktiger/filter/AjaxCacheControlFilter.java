package dk.drb.blacktiger.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebFilter;
/**
 * Filter for forcing Ajax requests to be non-cached. 
 * This filter is needed to prevent Internet Explorer from caching Ajax requests.  
 */
public class AjaxCacheControlFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest hreq = (HttpServletRequest) request;
        HttpServletResponse hresp = (HttpServletResponse) response;
        
        if("XMLHttpRequest".equals(hreq.getHeader("X-Requested-With"))) {
            hresp.addHeader("Cache-Control", "no-cache");
        }
        
        fc.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
    
    
}
