package com.vroomer.dashboard.config;

import jakarta.servlet.ServletContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class SpringContextInitializer implements ApplicationContextAware, ServletContextAware {

    private static ApplicationContext applicationContext;
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        System.out.println("[SpringContextInitializer] ServletContext received");
        if (applicationContext != null) {
            servletContext.setAttribute("springApplicationContext", applicationContext);
            System.out.println("[SpringContextInitializer] ApplicationContext stored in ServletContext (from setServletContext)");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
        System.out.println("[SpringContextInitializer] ApplicationContext received");
        if (servletContext != null) {
            servletContext.setAttribute("springApplicationContext", ctx);
            System.out.println("[SpringContextInitializer] ApplicationContext stored in ServletContext (from setApplicationContext)");
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
