package dk.drb.blacktiger.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration for Spring MVC Controllers.
 * This configuration class will scan for REST controllers and enable Spring Security Annotations for those.
 * It will also make sure that requests matching /static/** gets static content served directly.
 */
@Configuration
@ComponentScan("dk.drb.blacktiger.controller.rest") 
@EnableWebMvc
public class ControllerConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }
    
    
}
