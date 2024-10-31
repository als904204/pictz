package online.pictz.api.user.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final SiteUser siteUser;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(SiteUser siteUser, Map<String, Object> attributes) {
        this.siteUser = siteUser;
        this.attributes = Collections.unmodifiableMap(attributes);
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(siteUser.getRole().name()));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return siteUser.getProviderId();
    }

    public Long getSiteUserId() {
        return siteUser.getId();
    }

    public String getProviderId() {
        return siteUser.getProviderId();
    }
}
