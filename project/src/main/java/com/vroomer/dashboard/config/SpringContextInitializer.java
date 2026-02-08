package com.vroomer.dashboard.config;

import jakarta.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class SpringContextInitializer implements ApplicationContextAware, ServletContextAware {

    private static ApplicationContext applicationContext;
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        System.out.println("[SpringContextInitializer] ServletContext received");
        
        // Store the ApplicationContext in ServletContext if we have it
        // The ApplicationContext from setApplicationContext should already be set
        if (applicationContext != null) {
            servletContext.setAttribute("springApplicationContext", applicationContext);
            System.out.println("[SpringContextInitializer] ApplicationContext stored in ServletContext (from setServletContext)");
        } else {
            // Fallback: try to get from WebApplicationContextUtils
            try {
                WebApplicationContext webAppContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
                if (webAppContext != null) {
                    applicationContext = webAppContext;
                    servletContext.setAttribute("springApplicationContext", webAppContext);
                    System.out.println("[SpringContextInitializer] WebApplicationContext retrieved from ServletContext and stored");
                }
            } catch (Exception e) {
                System.err.println("[SpringContextInitializer] Error getting WebApplicationContext: " + e.getMessage());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        System.out.println("[SpringContextInitializer] ApplicationContext received: " + ctx.getClass().getName());
        
        // In Spring Boot, the ApplicationContext received here is usually the main application context
        // that contains all the beans scanned by @ComponentScan. This is what we want to use.
        applicationContext = ctx;
        System.out.println("[SpringContextInitializer] Storing ApplicationContext (contains application beans)");
        
        if (servletContext != null) {
            servletContext.setAttribute("springApplicationContext", applicationContext);
            System.out.println("[SpringContextInitializer] ApplicationContext stored in ServletContext (from setApplicationContext)");
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
