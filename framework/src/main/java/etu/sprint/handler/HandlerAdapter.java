package etu.sprint.handler;

import etu.sprint.model.ControllerMethod;
import etu.sprint.model.ModelView;
import etu.sprint.util.TypeConverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import etu.sprint.model.FileUpload;
import etu.sprint.model.Session;

import etu.sprint.annotation.RestAPI;
import etu.sprint.annotation.Authorized;
import etu.sprint.annotation.Token;
import etu.sprint.util.TokenValidator;
import etu.sprint.model.JsonResponse;
import etu.sprint.util.JsonConverter;
import etu.sprint.util.AuthorizationManager;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HandlerAdapter {
    private AuthorizationManager authorizationManager;
    public HandlerAdapter() {
        // AuthorizationManager sera initialisé avec ServletContext si nécessaire
    }

    public HandlerAdapter(ServletContext servletContext) {
        if (servletContext != null) {
            this.authorizationManager = new AuthorizationManager(servletContext);
        }
    }

    private void injectSpringDependencies(Object instance, ServletContext servletContext) {
        try {
            System.out.println("[HandlerAdapter] Starting dependency injection for: " + instance.getClass().getName());
            Object appContext = getSpringApplicationContext(servletContext);
            
            if (appContext == null) {
                System.err.println("[HandlerAdapter] WARNING: No Spring ApplicationContext found, cannot inject dependencies");
                System.err.println("[HandlerAdapter] Available ServletContext attributes:");
                java.util.Enumeration<String> attrNames = servletContext.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    System.err.println("  - " + attrNames.nextElement());
                }
                return;
            }

            System.out.println("[HandlerAdapter] Spring ApplicationContext found: " + appContext.getClass().getName());

            // Inject by field (prioritize fields with @Autowired annotation)
            Field[] fields = instance.getClass().getDeclaredFields();
            System.out.println("[HandlerAdapter] Found " + fields.length + " fields to check");
            
            for (Field field : fields) {
                field.setAccessible(true);
                
                // Skip if already set
                if (field.get(instance) != null) {
                    System.out.println("[HandlerAdapter] Field " + field.getName() + " already has a value, skipping");
                    continue;
                }
                
                System.out.println("[HandlerAdapter] Checking field: " + field.getName() + " of type " + field.getType().getName());
                
                // Check if field should be injected (has @Autowired or is a Spring-managed type)
                boolean hasAutowired = isAutowiredField(field);
                boolean isSpringManaged = isSpringManagedType(field.getType());
                boolean shouldInject = hasAutowired || isSpringManaged;
                
                System.out.println("[HandlerAdapter] Field " + field.getName() + " - @Autowired: " + hasAutowired + ", SpringManaged: " + isSpringManaged + ", ShouldInject: " + shouldInject);
                
                if (shouldInject) {
                    Object bean = getSpringBean(appContext, field.getType(), field.getName());
                    if (bean != null) {
                        field.set(instance, bean);
                        System.out.println("[HandlerAdapter] ✓ Successfully injected Spring bean: " + field.getType().getSimpleName() + " into " + field.getName());
                    } else {
                        System.err.println("[HandlerAdapter] ✗ Could not find Spring bean for field: " + field.getName() + " of type " + field.getType().getSimpleName());
                    }
                } else {
                    System.out.println("[HandlerAdapter] Skipping field " + field.getName() + " (not a Spring-managed type)");
                }
            }

            // Inject by setter methods
            for (Method method : instance.getClass().getMethods()) {
                if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    
                    // Only inject if it's a Spring-managed type
                    if (isSpringManagedType(paramType)) {
                        Object bean = getSpringBean(appContext, paramType, getFieldNameFromSetter(method.getName()));
                        if (bean != null) {
                            try {
                                method.invoke(instance, bean);
                                System.out.println("[HandlerAdapter] ✓ Injected Spring bean via setter: " + method.getName());
                            } catch (Exception e) {
                                System.err.println("[HandlerAdapter] ✗ Could not invoke setter " + method.getName() + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[HandlerAdapter] Error during Spring injection:");
            e.printStackTrace();
        }
    }

    private void verifyDependenciesInjected(Object instance) {
        try {
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(instance);
                
                // Check if field should be injected but is null
                if (value == null) {
                    boolean shouldBeInjected = isAutowiredField(field) || isSpringManagedType(field.getType());
                    if (shouldBeInjected) {
                        String errorMsg = String.format(
                            "WARNING: Dependency injection may have failed: Field '%s' of type '%s' in class '%s' is null. " +
                            "This field should have been injected by Spring but wasn't found in the ApplicationContext. " +
                            "The method will be called anyway, which may cause a NullPointerException.",
                            field.getName(), field.getType().getName(), instance.getClass().getName()
                        );
                        System.err.println("[HandlerAdapter] " + errorMsg);
                        // Don't throw - let the actual method call fail with a clearer error
                    }
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("[HandlerAdapter] Error verifying dependencies: " + e.getMessage());
        }
    }

    private Object getSpringApplicationContext(ServletContext servletContext) {
        Object appContext = null;

        // 1. Try SpringContextInitializer static method (most reliable - should have the application context)
        try {
            Class<?> initializerClass = Class.forName("com.vroomer.dashboard.config.SpringContextInitializer");
            Method getContextMethod = initializerClass.getMethod("getApplicationContext");
            appContext = getContextMethod.invoke(null);
            if (appContext != null) {
                System.out.println("[HandlerAdapter] Found Spring context via SpringContextInitializer.getApplicationContext()");
                return appContext;
            }
        } catch (Exception e) {
            System.out.println("[HandlerAdapter] SpringContextInitializer method not available: " + e.getMessage());
        }

        // 2. Try custom attribute set by SpringContextInitializer
        appContext = servletContext.getAttribute("springApplicationContext");
        if (appContext != null) {
            System.out.println("[HandlerAdapter] Found Spring context via servletContext attribute");
            return appContext;
        }

        // 3. Try to get the servlet-specific context (not ROOT) - this is where application beans are
        try {
            Class<?> utilsClass = Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
            // Try to get servlet context first (child context with beans)
            try {
                Method getServletContext = utilsClass.getMethod("getWebApplicationContext", ServletContext.class);
                appContext = getServletContext.invoke(null, servletContext);
                if (appContext != null) {
                    // Check if this context has a parent (ROOT) - if so, this is the servlet context with beans
                    try {
                        Method getParent = appContext.getClass().getMethod("getParent");
                        Object parent = getParent.invoke(appContext);
                        if (parent != null) {
                            System.out.println("[HandlerAdapter] Found servlet WebApplicationContext (has parent ROOT context)");
                            return appContext; // This is the servlet context with application beans
                        }
                    } catch (Exception e) {
                        // No parent method, continue
                    }
                    
                    // If no parent, this might be the ROOT context - try to find child contexts
                    System.out.println("[HandlerAdapter] Found WebApplicationContext, checking if it contains application beans...");
                    // We'll use this context but also check for child contexts in getSpringBean
                    return appContext;
                }
            } catch (Exception e) {
                System.err.println("[HandlerAdapter] WebApplicationContextUtils.getWebApplicationContext failed: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("[HandlerAdapter] WebApplicationContextUtils class not found: " + e.getMessage());
        }

        // 4. Try getRequiredWebApplicationContext
        try {
            Class<?> utilsClass = Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
            appContext = utilsClass.getMethod("getRequiredWebApplicationContext", ServletContext.class).invoke(null, servletContext);
            if (appContext != null) {
                System.out.println("[HandlerAdapter] Found Spring context via WebApplicationContextUtils.getRequiredWebApplicationContext");
                return appContext;
            }
        } catch (Exception e) {
            System.err.println("[HandlerAdapter] WebApplicationContextUtils.getRequiredWebApplicationContext failed: " + e.getMessage());
        }

        // 5. Try Spring ROOT attribute (but this might not have application beans)
        appContext = servletContext.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
        if (appContext != null) {
            System.out.println("[HandlerAdapter] Found Spring context via ROOT attribute (may not contain application beans)");
            return appContext;
        }

        // 6. Scan all servlet context attributes for any ApplicationContext
        java.util.Enumeration<String> attrNames = servletContext.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String name = attrNames.nextElement();
            Object attr = servletContext.getAttribute(name);
            if (attr != null && attr.getClass().getName().contains("ApplicationContext")) {
                System.out.println("[HandlerAdapter] Found Spring context via attribute scan: " + name);
                return attr;
            }
        }

        return null;
    }

    private Object getSpringBean(Object appContext, Class<?> beanType, String beanName) {
        System.out.println("[HandlerAdapter] Attempting to get Spring bean: type=" + beanType.getName() + ", name=" + beanName);
        
        // Try to get the context that contains the beans (might be child context, not parent)
        Object contextToUse = appContext;
        
        try {
            // Strategy: In Spring Boot, application beans are usually in the servlet context (child), not ROOT (parent)
            // So we should try the current context first, and if it has a parent, the current IS the servlet context
            
            // Try getBeansOfType first (this is the most reliable method - doesn't throw exceptions)
            try {
                Method getBeansOfType = contextToUse.getClass().getMethod("getBeansOfType", Class.class);
                Object beansMap = getBeansOfType.invoke(contextToUse, beanType);
                if (beansMap instanceof Map) {
                    Map<?, ?> beans = (Map<?, ?>) beansMap;
                    System.out.println("[HandlerAdapter] Found " + beans.size() + " beans of type " + beanType.getSimpleName() + " in current context");
                    if (!beans.isEmpty()) {
                        // Return the first bean found
                        Object bean = beans.values().iterator().next();
                        System.out.println("[HandlerAdapter] ✓ Found bean via getBeansOfType: " + beanType.getSimpleName());
                        return bean;
                    } else {
                        System.out.println("[HandlerAdapter] No beans found of type " + beanType.getSimpleName() + " in current context");
                        
                        // Check if this context has a parent - if so, we're in ROOT, need to check child contexts
                        // But actually, if we're here via SpringContextInitializer, we should have the right context
                        // Let's try to find all child contexts or check if we need to use a different approach
                        
                        // Try to find child contexts by checking ServletContext attributes
                        System.out.println("[HandlerAdapter] Current context might be ROOT, checking for servlet-specific contexts...");
                        
                        // Actually, if SpringContextInitializer was used, it should have the application context
                        // The issue might be that we're getting ROOT instead of the servlet context
                        // Let's try to get the servlet context explicitly
                        try {
                            try {
                                // We need to get ServletContext - but we don't have it here
                                // Instead, let's try to find child contexts by reflection
                                Method getParent = contextToUse.getClass().getMethod("getParent");
                                Object parentContext = getParent.invoke(contextToUse);
                                if (parentContext != null) {
                                    // We have a parent, so current is servlet context - but no beans found
                                    // This means beans might not be registered yet or in wrong package
                                    System.out.println("[HandlerAdapter] Current context has parent (is servlet context), but no beans found");
                                } else {
                                    // No parent means this IS the ROOT context
                                    System.out.println("[HandlerAdapter] Current context is ROOT (no parent), beans should be in servlet context");
                                    // We need to find the servlet context - but we can't from here
                                    // The SpringContextInitializer should have given us the right context
                                }
                            } catch (Exception e) {
                                System.out.println("[HandlerAdapter] Could not check parent context: " + e.getMessage());
                            }
                        } catch (Exception e) {
                            System.out.println("[HandlerAdapter] Error checking for servlet contexts: " + e.getMessage());
                        }
                        
                        // Debug: List all available beans and try to find the right context
                        try {
                            Method getAllBeans = contextToUse.getClass().getMethod("getBeansOfType", Class.class);
                            Object allBeansMap = getAllBeans.invoke(contextToUse, Object.class);
                            if (allBeansMap instanceof Map) {
                                Map<?, ?> allBeans = (Map<?, ?>) allBeansMap;
                                System.out.println("[HandlerAdapter] DEBUG: Total beans in current context: " + allBeans.size());
                                
                                // If we have very few beans (like < 10), this is probably ROOT context
                                // Try to get the servlet context from ServletContext
                                if (allBeans.size() < 10) {
                                    System.out.println("[HandlerAdapter] DEBUG: Current context has few beans, might be ROOT context");
                                    System.out.println("[HandlerAdapter] DEBUG: Available bean names:");
                                    int count = 0;
                                    for (Object beanNameObj : allBeans.keySet()) {
                                        if (count++ < 20) {
                                            System.out.println("  - " + beanNameObj);
                                        }
                                    }
                                    
                                    // Try to get servlet context - we need ServletContext for this
                                    // But we don't have it here, so we'll rely on SpringContextInitializer
                                    System.out.println("[HandlerAdapter] DEBUG: Trying to re-fetch context from SpringContextInitializer...");
                                    try {
                                        Class<?> initializerClass = Class.forName("com.vroomer.dashboard.config.SpringContextInitializer");
                                        Method getContextMethod = initializerClass.getMethod("getApplicationContext");
                                        Object newContext = getContextMethod.invoke(null);
                                        if (newContext != null && newContext != contextToUse) {
                                            System.out.println("[HandlerAdapter] DEBUG: Got different context from SpringContextInitializer, trying it...");
                                            // Try this new context
                                            Method getBeansOfTypeNew = newContext.getClass().getMethod("getBeansOfType", Class.class);
                                            Object newBeansMap = getBeansOfTypeNew.invoke(newContext, beanType);
                                            if (newBeansMap instanceof Map) {
                                                Map<?, ?> newBeans = (Map<?, ?>) newBeansMap;
                                                if (!newBeans.isEmpty()) {
                                                    Object bean = newBeans.values().iterator().next();
                                                    System.out.println("[HandlerAdapter] ✓ Found bean in context from SpringContextInitializer: " + beanType.getSimpleName());
                                                    return bean;
                                                }
                                            }
                                        }
                                    } catch (Exception e2) {
                                        System.out.println("[HandlerAdapter] DEBUG: Could not re-fetch from SpringContextInitializer: " + e2.getMessage());
                                    }
                                } else {
                                    System.out.println("[HandlerAdapter] DEBUG: Current context has many beans, should contain application beans");
                                }
                            }
                        } catch (Exception e) {
                            // Ignore debug errors
                        }
                    }
                }
            } catch (NoSuchMethodException e) {
                System.err.println("[HandlerAdapter] getBeansOfType method not found");
            } catch (Exception e) {
                System.err.println("[HandlerAdapter] Exception getting beans by type: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
            }

            // Try to get bean by type (getBean(Class.class)) - also try parent context
            Object contextToTry = appContext;
            for (int attempt = 0; attempt < 2; attempt++) {
                try {
                    Method getBeanByType = contextToTry.getClass().getMethod("getBean", Class.class);
                    Object bean = getBeanByType.invoke(contextToTry, beanType);
                    if (bean != null) {
                        System.out.println("[HandlerAdapter] ✓ Found bean by type (getBean) in " + (attempt == 0 ? "current" : "parent") + " context: " + beanType.getSimpleName());
                        return bean;
                    }
                } catch (NoSuchMethodException e) {
                    System.err.println("[HandlerAdapter] getBean(Class) method not found");
                    break;
                } catch (InvocationTargetException e) {
                    // This is likely a NoSuchBeanDefinitionException from Spring
                    Throwable cause = e.getCause();
                    if (cause != null && cause.getClass().getName().contains("NoSuchBeanDefinitionException")) {
                        if (attempt == 0) {
                            System.out.println("[HandlerAdapter] Bean not found by type in current context, trying parent...");
                            // Try parent context
                            try {
                                Method getParent = contextToTry.getClass().getMethod("getParent");
                                Object parentContext = getParent.invoke(contextToTry);
                                if (parentContext != null) {
                                    contextToTry = parentContext;
                                    continue;
                                }
                            } catch (Exception e2) {
                                System.out.println("[HandlerAdapter] Could not access parent context: " + e2.getMessage());
                            }
                        } else {
                            System.out.println("[HandlerAdapter] Bean not found by type (NoSuchBeanDefinitionException): " + cause.getMessage());
                        }
                    } else {
                        System.err.println("[HandlerAdapter] Error getting bean by type: " + (cause != null ? cause : e));
                    }
                    break;
                } catch (Exception e) {
                    System.err.println("[HandlerAdapter] Exception getting bean by type: " + e.getClass().getName() + " - " + e.getMessage());
                    break;
                }
            }

            // Try to get bean by name (using field name)
            try {
                Method getBeanByName = appContext.getClass().getMethod("getBean", String.class);
                Object bean = getBeanByName.invoke(appContext, beanName);
                if (bean != null && beanType.isInstance(bean)) {
                    System.out.println("[HandlerAdapter] ✓ Found bean by name (field name): " + beanName);
                    return bean;
                }
            } catch (InvocationTargetException e) {
                // Bean not found by this name, continue
            } catch (Exception e) {
                System.err.println("[HandlerAdapter] Exception getting bean by name (" + beanName + "): " + e.getMessage());
            }

            // Try with camelCase class name (e.g., ReservationService -> reservationService)
            try {
                String camelCaseName = Character.toLowerCase(beanType.getSimpleName().charAt(0)) + 
                                      beanType.getSimpleName().substring(1);
                Method getBeanByName = appContext.getClass().getMethod("getBean", String.class);
                Object bean = getBeanByName.invoke(appContext, camelCaseName);
                if (bean != null && beanType.isInstance(bean)) {
                    System.out.println("[HandlerAdapter] ✓ Found bean by name (camelCase): " + camelCaseName);
                    return bean;
                }
            } catch (InvocationTargetException e) {
                // Bean not found by this name, continue
            } catch (Exception e) {
                System.err.println("[HandlerAdapter] Exception getting bean by camelCase name: " + e.getMessage());
            }

            System.err.println("[HandlerAdapter] ✗ Could not find Spring bean for type: " + beanType.getName() + " with name: " + beanName);
            
        } catch (Exception e) {
            System.err.println("[HandlerAdapter] Unexpected error in getSpringBean: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    private boolean isAutowiredField(Field field) {
        try {
            // Check for Spring's @Autowired annotation
            @SuppressWarnings("unchecked")
            Class<? extends java.lang.annotation.Annotation> autowiredClass = 
                (Class<? extends java.lang.annotation.Annotation>) Class.forName("org.springframework.beans.factory.annotation.Autowired");
            return field.isAnnotationPresent(autowiredClass);
        } catch (ClassNotFoundException | ClassCastException e) {
            return false;
        }
    }

    private boolean isSpringManagedType(Class<?> type) {
        // Check if the type is likely a Spring-managed bean
        // This includes classes annotated with @Service, @Component, @Repository, @Controller
        try {
            String[] springAnnotations = {
                "org.springframework.stereotype.Service",
                "org.springframework.stereotype.Component",
                "org.springframework.stereotype.Repository",
                "org.springframework.stereotype.Controller"
            };
            
            for (String annotationName : springAnnotations) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends java.lang.annotation.Annotation> annotationClass = 
                        (Class<? extends java.lang.annotation.Annotation>) Class.forName(annotationName);
                    if (type.isAnnotationPresent(annotationClass)) {
                        System.out.println("[HandlerAdapter] Type " + type.getName() + " is annotated with " + annotationName);
                        return true;
                    }
                } catch (ClassNotFoundException | ClassCastException e) {
                    // Annotation class not found, continue
                }
            }
        } catch (Exception e) {
            // Error checking annotations
        }
        
        // Also check if it's in a package that likely contains Spring beans
        // For now, we'll try to inject any non-primitive, non-JDK type that ends with "Service", "Repository", "Component", etc.
        String typeName = type.getSimpleName();
        String typePackage = type.getPackage() != null ? type.getPackage().getName() : "";
        
        // Check if it's a common Spring bean naming pattern
        boolean isCommonSpringBean = typeName.endsWith("Service") || 
                                     typeName.endsWith("Repository") || 
                                     typeName.endsWith("Component") ||
                                     typeName.endsWith("Controller") ||
                                     typeName.endsWith("Manager") ||
                                     typeName.endsWith("Dao");
        
        // Check if it's in a package that likely contains Spring beans (not JDK, not framework)
        boolean isInApplicationPackage = !type.isPrimitive() && 
                                        !type.getName().startsWith("java.") && 
                                        !type.getName().startsWith("jakarta.") &&
                                        !type.getName().startsWith("javax.") &&
                                        !type.getName().startsWith("etu.sprint.") &&
                                        (typePackage.contains("service") || 
                                         typePackage.contains("repository") || 
                                         typePackage.contains("component") ||
                                         typePackage.contains("controller") ||
                                         isCommonSpringBean);
        
        if (isInApplicationPackage) {
            System.out.println("[HandlerAdapter] Type " + type.getName() + " appears to be a Spring-managed type (package/name pattern)");
        }
        
        return isInApplicationPackage;
    }

    private String getFieldNameFromSetter(String setterName) {
        if (setterName.startsWith("set") && setterName.length() > 3) {
            String fieldName = setterName.substring(3);
            return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
        return setterName;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, ControllerMethod controllerMethod,
                        Map<String, String> pathVariables) throws ServletException, IOException {
        try {
            Method method = controllerMethod.method;

            // Vérifier le token avant d'exécuter la méthode
            if (method.isAnnotationPresent(Token.class)) {
                String authToken = request.getHeader("Authorization");
                if (authToken == null || authToken.isEmpty()) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().print("{\"status\":\"error\",\"code\":401,\"message\":\"Token manquant dans le header Authorization\",\"data\":null}");
                    return;
                }
                // Valider le token via TokenValidator du framework (JDBC direct)
                Object appCtx = getSpringApplicationContext(request.getServletContext());
                boolean tokenValid = TokenValidator.isTokenValid(appCtx, authToken);
                if (!tokenValid) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().print("{\"status\":\"error\",\"code\":403,\"message\":\"Token invalide ou expiré\",\"data\":null}");
                    return;
                }
            }

            // Vérifier l'autorisation avant d'exécuter la méthode
            if (method.isAnnotationPresent(Authorized.class)) {
                if (authorizationManager == null) {
                    // Initialiser avec le ServletContext de la requête si pas encore fait
                    authorizationManager = new AuthorizationManager(request.getServletContext());
                }
                Authorized authorizedAnnotation = method.getAnnotation(Authorized.class);
                if (!authorizationManager.isAuthorized(request, response, authorizedAnnotation)) {
                    // La réponse d'erreur a déjà été envoyée par AuthorizationManager
                    return;
                }
            }
            // Try to get controller instance from Spring first (supports constructor injection)
            Object controllerInstance = null;
            Object appContext = getSpringApplicationContext(request.getServletContext());
            if (appContext != null) {
                try {
                    java.lang.reflect.Method getBeanMethod = appContext.getClass().getMethod("getBean", Class.class);
                    controllerInstance = getBeanMethod.invoke(appContext, controllerMethod.controllerClass);
                    System.out.println("[HandlerAdapter] ✓ Got controller from Spring: " + controllerMethod.controllerClass.getSimpleName());
                } catch (Exception e) {
                    System.out.println("[HandlerAdapter] Controller not in Spring context, creating manually: " + e.getMessage());
                }
            }
            
            if (controllerInstance == null) {
                // Fallback: create with no-arg constructor + manual injection
                controllerInstance = controllerMethod.controllerClass.getDeclaredConstructor().newInstance();
                injectSpringDependencies(controllerInstance, request.getServletContext());
                verifyDependenciesInjected(controllerInstance);
            }
            
            Parameter[] parameters = method.getParameters();
            Object[] args = new Object[parameters.length];
            Map<String, String[]> requestParams = request.getParameterMap();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.getType() == Session.class || parameter.isAnnotationPresent(etu.sprint.annotation.Session.class)) {
                    handleSessionParameter(i, args, request);
                } else if (parameter.isAnnotationPresent(etu.sprint.annotation.RequestParameter.class)) {
                    handleRequestParameter(parameter, i, args, pathVariables, requestParams);
                } else if (parameter.getType() == Map.class) {
                    handleMapParameter(i, args, pathVariables, requestParams);
                } else if (parameter.getType() == FileUpload.class) {
                     handleFileUploadParameter(parameter, i, args, request);
                } else if (parameter.getType() == HttpServletRequest.class) {
                     args[i] = request;
                } else if (isComplexType(parameter.getType())) {
                    args[i] = handleComplexType(parameter.getType(), requestParams);
                } else {
                    handleDefaultParameter(parameter, i, args, pathVariables, requestParams);
                }
            }



            Object returnValue;
            try {
                returnValue = method.invoke(controllerInstance, args);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NullPointerException) {
                    // Check if it's a null dependency issue
                    String errorMsg = "NullPointerException in controller method. This is likely due to a missing dependency injection. " +
                                    "Please check the logs above for dependency injection errors.";
                    System.err.println("[HandlerAdapter] " + errorMsg);
                    System.err.println("[HandlerAdapter] Original exception: " + cause.getMessage());
                    cause.printStackTrace();
                    throw new ServletException(errorMsg, cause);
                }
                throw e;
            }

            if (method.isAnnotationPresent(RestAPI.class)) {
                JsonResponse jsonResponse = new JsonResponse("success", 200, "OK", returnValue);
                String json = JsonConverter.toJson(jsonResponse);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                out.print(json);
                out.flush();
            } else {
                if (returnValue instanceof String) {
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().println(returnValue);
                } else if (returnValue instanceof ModelView) {
                    ModelView mv = (ModelView) returnValue;
                    String view = mv.getView();
                    // Gestion native de la redirection
                    if (view != null && view.startsWith("redirect:")) {
                        String redirectUrl = view.substring("redirect:".length());
                        response.sendRedirect(redirectUrl);
                        return;
                    }
                    // Rendu classique avec ViewResolver pour traiter les templates personnalisés
                    etu.sprint.view.ViewResolver viewResolver = new etu.sprint.view.ViewResolver();
                    viewResolver.render(mv, request.getServletContext(), response);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ServletException("Erreur lors de l'execution de la methode du controleur", e);
        }
    }



    private void handleRequestParameter(Parameter parameter, int index, Object[] args,
                                        Map<String, String> pathVariables, Map<String, String[]> requestParams) {
        etu.sprint.annotation.RequestParameter rp = parameter.getAnnotation(etu.sprint.annotation.RequestParameter.class);
        String paramName = rp.value();
        String paramValue = pathVariables.getOrDefault(paramName, requestParams.containsKey(paramName) ? requestParams.get(paramName)[0] : null);
        args[index] = TypeConverter.convertStringValue(paramValue, parameter.getType());
    }


    private void handleMapParameter(int index, Object[] args,
                                    Map<String, String> pathVariables, Map<String, String[]> requestParams) {
        Map<String, Object> dataMap = new HashMap<>();
        pathVariables.forEach(dataMap::put);
        requestParams.forEach((key, values) -> {
            if (values.length == 1) {
                dataMap.put(key, values[0]);
            } else {
                dataMap.put(key, Arrays.asList(values));
            }
        });
        args[index] = dataMap;
    }



    private Object handleComplexType(Class<?> type, Map<String, String[]> requestParams)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object instance = type.getDeclaredConstructor().newInstance();
        for (Field field : type.getDeclaredFields()) {
            String fieldName = field.getName();
            String[] paramValues = requestParams.get(fieldName);
            if (paramValues != null) {
                try {
                    Method setter = type.getMethod("set" + capitalize(fieldName), field.getType());
                    if (field.getType() == List.class) {
                        setter.invoke(instance, Arrays.asList(paramValues));
                    } else if (paramValues.length > 0) {
                        Object convertedValue = TypeConverter.convertStringValue(paramValues[0], field.getType());
                        setter.invoke(instance, convertedValue);
                    }
                } catch (NoSuchMethodException e) {
                    // Ignorer si le setter n'existe pas
                }
            }
        }
        return instance;
    }



    private void handleDefaultParameter(Parameter parameter, int index, Object[] args,
                                        Map<String, String> pathVariables, Map<String, String[]> requestParams) {
        String paramName = parameter.getName();
        String paramValue = pathVariables.getOrDefault(paramName, requestParams.containsKey(paramName) ? requestParams.get(paramName)[0] : null);
        args[index] = TypeConverter.convertStringValue(paramValue, parameter.getType());
    }



    private boolean isComplexType(Class<?> type) {
        return !type.isPrimitive() && type != String.class && !Number.class.isAssignableFrom(type) && type != Boolean.class && !type.getPackage().getName().startsWith("java.lang");
    }



    private void handleFileUploadParameter(Parameter parameter, int index, Object[] args, HttpServletRequest request) throws ServletException, IOException {
        String paramName = parameter.getName();
        etu.sprint.annotation.RequestParameter rp = parameter.getAnnotation(etu.sprint.annotation.RequestParameter.class);
        if (rp != null) {
            paramName = rp.value();
        }
        try {
            Part part = request.getPart(paramName);
            if (part != null) {
                FileUpload fileUpload = new FileUpload();
                fileUpload.setFileName(part.getSubmittedFileName());
                fileUpload.setBytes(part.getInputStream().readAllBytes());
                fileUpload.setContentType(part.getContentType());
                args[index] = fileUpload;
            }
        } catch (Exception e) {
             // Handle exceptions or ignore if part is missing/invalid
             // e.printStackTrace();
        }
    }

    private void handleSessionParameter(int index, Object[] args, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        Session session = new Session(httpSession);
        args[index] = session;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

}
