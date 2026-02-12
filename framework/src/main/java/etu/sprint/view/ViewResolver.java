package etu.sprint.view;

import etu.sprint.model.ModelView;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewResolver {

    private ServletContext servletContext;

    public void render(ModelView modelView, ServletContext context, HttpServletResponse response) throws IOException {
        this.servletContext = context;
        String viewPath = modelView.getView();
        if (!viewPath.startsWith("/")) {
            viewPath = "/" + viewPath;
        }

        // Try multiple locations to find the view
        String[] pathsToTry = {
            viewPath,
            "/static" + viewPath,
            "/WEB-INF/views" + viewPath,
            "/templates" + viewPath // Spring Boot/Thymeleaf default
        };

        InputStream is = null;
        String usedPath = null;
        
        for (String path : pathsToTry) {
            is = context.getResourceAsStream(path);
            if (is != null) {
                usedPath = path;
                break;
            }
        }

        // Fallback: Try ClassLoader (useful for packaged JARs/Spring Boot)
        if (is == null) {
            // If path already starts with /templates, don't prepend again
            String classLoaderPath;
            if (viewPath.startsWith("/templates/")) {
                classLoaderPath = viewPath.substring(1); // remove leading /
            } else {
                classLoaderPath = "templates" + viewPath;
            }
            if (classLoaderPath.startsWith("/")) {
                classLoaderPath = classLoaderPath.substring(1);
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classLoaderPath);
            if (is != null) {
                usedPath = "classpath:" + classLoaderPath;
            }
        }
        
        // Fallback: Try ClassLoader with static prefix
        if (is == null) {
            String classLoaderPath = "static" + viewPath; // e.g. static/pages/reservation.html
            if (classLoaderPath.startsWith("/")) {
                classLoaderPath = classLoaderPath.substring(1);
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classLoaderPath);
            if (is != null) {
                 usedPath = "classpath:" + classLoaderPath;
            }
        }
        
        if (is == null) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("View not found: " + viewPath + " (tried: " + String.join(", ", pathsToTry) + ", classpath:static" + viewPath + ")");
            return;
        }

        StringBuilder templateContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line).append("\n");
            }
        }

        String processedContent = processTemplate(templateContent.toString(), modelView.getData());

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(processedContent);
        out.flush();
    }

    private String processTemplate(String content, Map<String, Object> data) {
        // 1. Process <framework:include src="..."/> tags (top-level only)
        Pattern includePattern = Pattern.compile("<framework:include\\s+src=\"([^\"]+)\"\\s*/?>", Pattern.DOTALL);
        Matcher includeMatcher = includePattern.matcher(content);
        StringBuilder includeResult = new StringBuilder();
        int lastIncludeEnd = 0;
        while (includeMatcher.find()) {
            includeResult.append(content, lastIncludeEnd, includeMatcher.start());
            String fragmentPath = includeMatcher.group(1);
            String fragmentContent = loadFragment(fragmentPath);
            if (fragmentContent != null) {
                includeResult.append(fragmentContent);
            } else {
                includeResult.append("<!-- Fragment not found: " + fragmentPath + " -->");
            }
            lastIncludeEnd = includeMatcher.end();
        }
        includeResult.append(content.substring(lastIncludeEnd));

        // 2. Process Foreach Loops: <framework:foreach items="${list}" var="item"> ... </framework:foreach>
        Pattern loopPattern = Pattern.compile("<framework:foreach\\s+items=\"\\$\\{(\\w+)}\"\\s+var=\"(\\w+)\">(.*?)</framework:foreach>", Pattern.DOTALL);
        Matcher loopMatcher = loopPattern.matcher(includeResult.toString());
        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        while (loopMatcher.find()) {
            result.append(includeResult.toString(), lastMatchEnd, loopMatcher.start());
            String listName = loopMatcher.group(1);
            String varName = loopMatcher.group(2);
            String loopBody = loopMatcher.group(3);
            Object listObj = data.get(listName);
            if (listObj instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) listObj;
                for (Object item : collection) {
                    Map<String, Object> loopData = new HashMap<>(data);
                    loopData.put(varName, item);
                    String processedBody = processVariables(loopBody, loopData);
                    result.append(processedBody);
                }
            }
            lastMatchEnd = loopMatcher.end();
        }
        result.append(includeResult.toString().substring(lastMatchEnd));

        // 3. Process remaining variables in the main content
        return processVariables(result.toString(), data);
    }

    // Loads fragment content from servlet context or classpath
    private String loadFragment(String fragmentPath) {
        String normalizedPath = fragmentPath;
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        InputStream is = null;

        // 1. Try ServletContext with multiple paths
        if (this.servletContext != null) {
            String[] pathsToTry = {
                normalizedPath,
                "/templates" + normalizedPath,
                "/WEB-INF" + normalizedPath
            };
            for (String path : pathsToTry) {
                is = this.servletContext.getResourceAsStream(path);
                if (is != null) break;
            }
        }

        // 2. Fallback: classloader (for JAR deployment)
        if (is == null) {
            String classLoaderPath = "templates" + normalizedPath;
            if (classLoaderPath.startsWith("/")) {
                classLoaderPath = classLoaderPath.substring(1);
            }
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classLoaderPath);
        }

        if (is == null) {
            return null;
        }
        StringBuilder fragmentContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fragmentContent.append(line).append("\n");
            }
        } catch (IOException e) {
            return null;
        }
        return fragmentContent.toString();
    }
    private String processVariables(String content, Map<String, Object> data) {
        // Match ${variable} or ${variable.field}
        Pattern variablePattern = Pattern.compile("\\$\\{([\\w.]+)}");
        Matcher matcher = variablePattern.matcher(content);
        
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String variablePath = matcher.group(1);
            String value = resolveVariable(variablePath, data);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String resolveVariable(String path, Map<String, Object> data) {
        String[] parts = path.split("\\.");
        String rootKey = parts[0];
        
        Object obj = data.get(rootKey);
        if (obj == null) {
            return ""; // Variable not found
        }
        
        if (parts.length == 1) {
            return obj.toString();
        }
        
        // Traverse fields for nested properties
        for (int i = 1; i < parts.length; i++) {
            if (obj == null) break;
            obj = getFieldValue(obj, parts[i]);
        }
        
        return obj != null ? obj.toString() : "";
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            // Try getter first
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method method = obj.getClass().getMethod(getterName);
            return method.invoke(obj);
        } catch (Exception e) {
            try {
                // Try direct field access
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
