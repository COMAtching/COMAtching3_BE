package comatching.comatching3.users.auth.oauth2.dto;

import lombok.Data;

import java.util.Map;

@Data
public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> profileAttributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>)attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>)kakaoAccountAttributes.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    public String getNickname() {
        return profileAttributes.get("nickname").toString();
    }
}
