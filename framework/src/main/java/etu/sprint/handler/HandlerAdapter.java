package etu.sprint.handler;

import etu.sprint.model.ControllerMethod;
import etu.sprint.model.ModelView;
import etu.sprint.util.TypeConverter;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import etu.sprint.model.FileUpload;
import etu.sprint.model.Session;

import etu.sprint.annotation.RestAPI;
import etu.sprint.annotation.Authorized;
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
            Object appContext = null;

            // 1. Try custom attribute set by SpringContextInitializer
            appContext = servletContext.getAttribute("springApplicationContext");
            if (appContext != null) {
                System.out.println("[HandlerAdapter] Found Spring context via servletContext attribute");
            }

            // 2. Try Spring ROOT attribute
            if (appContext == null) {
                appContext = servletContext.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
                if (appContext != null) {
                    System.out.println("[HandlerAdapter] Found Spring context via ROOT attribute");
                }
            }

            // 3. Try WebApplicationContextUtils via reflection
            if (appContext == null) {
                try {
                    Class<?> utilsClass = Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
                    appContext = utilsClass.getMethod("getRequiredWebApplicationContext", ServletContext.class).invoke(null, servletContext);
                    if (appContext != null) {
                        System.out.println("[HandlerAdapter] Found Spring context via WebApplicationContextUtils");
                    }
                } catch (Exception e) {
                    System.err.println("[HandlerAdapter] WebApplicationContextUtils failed: " + e.getMessage());
                }
            }

            // 4. Scan all servlet context attributes for any ApplicationContext
            if (appContext == null) {
                java.util.Enumeration<String> attrNames = servletContext.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    String name = attrNames.nextElement();
                    Object attr = servletContext.getAttribute(name);
                    if (attr != null && attr.getClass().getName().contains("ApplicationContext")) {
                        appContext = attr;
                        System.out.println("[HandlerAdapter] Found Spring context via attribute scan: " + name);
                        break;
                    }
                }
            }

            if (appContext == null) {
                System.err.println("[HandlerAdapter] WARNING: No Spring ApplicationContext found, cannot inject dependencies");
                System.err.println("[HandlerAdapter] Available attributes:");
                java.util.Enumeration<String> attrNames = servletContext.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    System.err.println("  - " + attrNames.nextElement());
                }
                return;
            }

            // For each field in the controller, try to inject a Spring bean by type
            for (Field field : instance.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(instance) != null) continue; // already set, skip
                try {
                    Object bean = appContext.getClass().getMethod("getBean", Class.class).invoke(appContext, field.getType());
                    if (bean != null) {
                        field.set(instance, bean);
                        System.out.println("[HandlerAdapter] Injected Spring bean: " + field.getType().getSimpleName() + " into " + field.getName());
                    }
                } catch (Exception e) {
                    System.err.println("[HandlerAdapter] Could not inject " + field.getType().getSimpleName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[HandlerAdapter] Error during Spring injection:");
            e.printStackTrace();
        }
    }

    public void handle(HttpServletRequest request, HttpServletResponse response, ControllerMethod controllerMethod,
                        Map<String, String> pathVariables) throws ServletException, IOException {
        try {
            Method method = controllerMethod.method;
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
            Object controllerInstance = controllerMethod.controllerClass.getDeclaredConstructor().newInstance();
            
            // Inject Spring dependencies if available
            injectSpringDependencies(controllerInstance, request.getServletContext());
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



            Object returnValue = method.invoke(controllerInstance, args);

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
                    // Si la vue est un fichier HTML, on sert le fichier statique correspondant
                    if (view != null && view.endsWith(".html")) {
                        ServletContext context = request.getServletContext();
                        String staticPath = view.startsWith("/") ? view : "/" + view;
                        java.io.InputStream is = context.getResourceAsStream(staticPath);
                        if (is == null) {
                            // Try templates directory if not found
                            staticPath = "/templates" + staticPath;
                            is = context.getResourceAsStream(staticPath);
                        }
                        if (is != null) {
                            response.setContentType("text/html;charset=UTF-8");
                            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.getWriter().println(line);
                            }
                            reader.close();
                            return;
                        }
                    }
                    // Sinon, rendu classique
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
