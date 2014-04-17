package dk.drb.blacktiger;

import dk.drb.blacktiger.filter.AjaxCacheControlFilter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext appContext = getContext();
        servletContext.addListener(new ContextLoaderListener(appContext));
        applyDispatcherServlet(servletContext, appContext);
        applyCorsFilter(servletContext);
        applyAjaxCacheFilter(servletContext);
        applySecurityFilter(servletContext);
    }

    private void applyAjaxCacheFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("ajaxFilter", AjaxCacheControlFilter.class);
        filter.setAsyncSupported(true);
        filter.addMappingForUrlPatterns(null, true, "/*");
    }

    private void applySecurityFilter(ServletContext servletContext) {
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy("springSecurityFilterChain");
	FilterRegistration.Dynamic filter = servletContext.addFilter("securityFilter", delegatingFilterProxy);
        filter.setAsyncSupported(true);
        filter.addMappingForUrlPatterns(null, true, "/*");
    }

    private void applyCorsFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic filter = servletContext.addFilter("corsFilter", "org.apache.catalina.filters.CorsFilter");
        filter.setInitParameter("cors.allowed.origins", "*");
        filter.setInitParameter("cors.allowed.methods", "GET,POST,HEAD,OPTIONS,PUT,DELETE");
        filter.setInitParameter("cors.allowed.headers", 
                "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
        filter.setInitParameter("cors.exposed.headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials");
        filter.setInitParameter("cors.support.credentials", "true");
        filter.setInitParameter("cors.preflight.maxage", "10");
        filter.setAsyncSupported(true);
        filter.addMappingForUrlPatterns(null, true, "/rooms/*", "/system/*", "/users/*", "/reports/*", "/phonebook/*", "/sipaccounts/*");
    }
    
    private void applyDispatcherServlet(ServletContext servletContext, WebApplicationContext appContext) {
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(appContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        dispatcher.setAsyncSupported(true);
    }
    
    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("dk.drb.blacktiger.config");
        return context;
    }
    
}
