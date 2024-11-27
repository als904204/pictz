package online.pictz.api.common.resolver;

import lombok.RequiredArgsConstructor;
import online.pictz.api.common.annotation.CurrentUser;
import online.pictz.api.user.dto.UserDto;
import online.pictz.api.user.entity.CustomOAuth2User;
import online.pictz.api.user.exception.UserUnauthorized;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
            UserDto.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw UserUnauthorized.of();
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User customUser = (CustomOAuth2User) principal;
            return new UserDto(customUser.getSiteUserId(), customUser.getName());
        }

        throw UserUnauthorized.of();
    }
}
