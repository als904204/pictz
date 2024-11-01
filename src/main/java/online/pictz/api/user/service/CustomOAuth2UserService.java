package online.pictz.api.user.service;


import lombok.RequiredArgsConstructor;
import online.pictz.api.user.entity.CustomOAuth2User;
import online.pictz.api.user.entity.SiteUser;
import online.pictz.api.user.repository.SiteUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 인증 후 사용자 회원가입 or 로그인
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final SiteUserRepository siteUserRepository;
    private final NicknameGenerator nicknameGenerator;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        var attributes = oAuth2User.getAttributes();

        String providerId = (String) attributes.get(userNameAttributeName);

        SiteUser siteUser = siteUserRepository.findByProviderId(providerId)
            .orElseGet(() -> {
                String randomNickname = nicknameGenerator.generateNickname();
                SiteUser newUser = new SiteUser(randomNickname, providerId);
                return siteUserRepository.save(newUser);
            });

        return new CustomOAuth2User(siteUser, attributes);
    }

}