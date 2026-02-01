package etu.sprint.config;

import etu.sprint.model.ModelView;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Handler pour interpréter les retours ModelView du framework maison
 * et effectuer une redirection vers la vue HTML correspondante.
 */
public class ModelViewReturnValueHandler implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return ModelView.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType,
                                   ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if (returnValue instanceof ModelView modelView) {
            mavContainer.setRequestHandled(true);
            
            HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
            if (response != null) {
                // Rediriger vers la vue HTML statique
                String viewPath = "/" + modelView.getView();
                response.sendRedirect(viewPath);
            }
        }
    }
}
