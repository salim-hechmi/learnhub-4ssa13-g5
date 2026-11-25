package tn.esprit.learnhub.marketplace.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import tn.esprit.learnhub.marketplace.exception.UnauthenticatedException;
import tn.esprit.learnhub.marketplace.util.CurrentUser;

/**
 * Injecte automatiquement {@link CurrentUser} dans les contrôleurs à partir des en-têtes
 * X-User-Id / X-User-Roles / X-User-Email ajoutés par l'API Gateway après validation du JWT.
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return CurrentUser.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) {
        String id = request.getHeader(CurrentUser.HEADER_ID);
        if (!StringUtils.hasText(id)) {
            throw new UnauthenticatedException(
                    "Identité absente : en-tête " + CurrentUser.HEADER_ID + " manquant (appel hors API Gateway ?)");
        }
        return CurrentUser.of(id.trim(),
                request.getHeader(CurrentUser.HEADER_ROLES),
                request.getHeader(CurrentUser.HEADER_EMAIL));
    }
}
