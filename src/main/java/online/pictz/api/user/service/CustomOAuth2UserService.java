package online.pictz.api.user.service;

import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.pictz.api.user.entity.SiteUser;
import online.pictz.api.user.entity.SiteUserRole;
import online.pictz.api.user.repository.SiteUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final SiteUserRepository siteUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        var attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get(userNameAttributeName);

        Optional<SiteUser> optionalUser = siteUserRepository.findByProviderId(providerId);

        SiteUser siteUser;
        if (optionalUser.isPresent()) {
            siteUser = optionalUser.get();
        } else {
            siteUser = SiteUser.builder()
                .providerId(providerId)
                .role(SiteUserRole.ROLE_USER)
                .build();
            siteUserRepository.save(siteUser);
        }

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(siteUser.getRole().name())),
            attributes,
            userNameAttributeName
        );
    }
}