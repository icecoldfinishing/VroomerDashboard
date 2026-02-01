package etu.sprint.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration MVC pour intégrer le framework maison avec Spring Boot.
 * Enregistre le handler ModelView en priorité pour interpréter les retours des contrôleurs.
 */
@Configuration
public class WebMvcConfig {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @PostConstruct
    public void init() {
        // Insérer notre handler ModelView en premier (prioritaire)
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
        handlers.add(new ModelViewReturnValueHandler());
        handlers.addAll(requestMappingHandlerAdapter.getReturnValueHandlers());
        requestMappingHandlerAdapter.setReturnValueHandlers(handlers);
    }
}
