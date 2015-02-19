package dk.drb.blacktiger.util;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;


public class SimpleFilterConfig implements FilterConfig {

    private final String name;
    private final ServletContext servletContext;
    private final Map<String, String> config;

    public SimpleFilterConfig(String name, ServletContext servletContext) {
        this.name = name;
        this.servletContext = servletContext;
        this.config = null;
    }

    public SimpleFilterConfig(String name, ServletContext servletContext, Map<String, String> config) {
        this.name = name;
        this.servletContext = servletContext;
        this.config = config;
    }
    
    @Override
    public String getFilterName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return config == null ? null : config.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return config == null ? new Vector<String>().elements() : new Vector<>(config.keySet()).elements();
    }

}