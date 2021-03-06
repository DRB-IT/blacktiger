package dk.drb.blacktiger;

import dk.drb.blacktiger.config.AppConfig;
import dk.drb.blacktiger.config.ControllerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(AppInitializer.class);
    
    /*@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext appContext = getContext();
        servletContext.addListener(new ContextLoaderListener(appContext));
        applyCorsFilter(servletContext);
        applyDispatcherServlet(servletContext, appContext);
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

    private void applyCorsFilter(ServletContext servletContext) throws ServletException {
        FilterRegistration.Dynamic filter = servletContext.addFilter("corsFilter", "org.apache.catalina.filters.CorsFilter");
        filter.setInitParameter("cors.allowed.origins", "*");
        filter.setInitParameter("cors.allowed.methods", "GET,POST,HEAD,OPTIONS,PUT,DELETE");
        filter.setInitParameter("cors.allowed.headers", 
                "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
        filter.setInitParameter("cors.exposed.headers", "Access-Control-Allow-Origin,Access-Control-Allow-Credentials");
        filter.setInitParameter("cors.support.credentials", "true");
        filter.setInitParameter("cors.preflight.maxage", "10");
        filter.setAsyncSupported(true);
        filter.addMappingForUrlPatterns(null, true, "/rooms/*", "/system/*", "/users/*", "/reports/*", "/phonebook/*", "/sipaccounts/*", "/participants");
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
    }*/

    @Override
    protected Class<?>[] getRootConfigClasses() {
        LOG.info("Returning root config classes.");
        return new Class<?>[]{AppConfig.class};
    }
    
    @Override
    protected Class<?>[] getServletConfigClasses() {
        
        LOG.info("Returning servlet config classes.");
        return new Class<?>[]{ControllerConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        LOG.info("Returning serlvet mappings.");
        return new String[]{"/*", "/*"};
    }
    
}
