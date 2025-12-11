package ma.ip.services;

import lombok.extern.log4j.Log4j2;
import ma.ip.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.Base64;


@Service
@Log4j2
@Lazy
public class TokenApigeeServiceImpl implements TokenApigeeService {
    private String token;
    private LocalDateTime tokenExpiresAt;

    @Value("${ip.authentication.apigee.oauth2.token_url:}")
    private String oauth2TokenUrl;
    @Value("${ip.authentication.apigee.oauth2.client_id:}")
    private String oauth2ClientId;
    @Value("${ip.authentication.apigee.oauth2.client_secret:}")
    private String oauth2ClientSecret;
    @Value("${ip.authentication.apigee.oauth2.scope:third-parties accounts}")
    private String oauth2Scope;
    @Value("${ip.authorization.apigee.oauth2.prefix:Bearer}")
    private String authorizationOauth2TokenPrefix;
    @Value("${ip.authorization.apigee.oauth2.enabled:true}")
    private boolean apigeeTokenEnabled;


    @Override
    public String getToken() {

        if (apigeeTokenEnabled) {
            LocalDateTime currentTime = LocalDateTime.now();
            if (token == null || token.isEmpty() || tokenExpiresAt.isBefore(currentTime)) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                String clientIdSecret = oauth2ClientId + ":" + oauth2ClientSecret;
                byte[] encode = Base64.getEncoder().encode(clientIdSecret.getBytes());
                String baiscToken = new String(encode);
                headers.add("Authorization", "Basic " + baiscToken);
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type", "client_credentials");
                map.add("scope", oauth2Scope);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<TokenDto> response = restTemplate.exchange(oauth2TokenUrl, HttpMethod.POST, entity, TokenDto.class);
                token = response.getBody().getAccess_token();
                int expiresIn = response.getBody().getExpires_in();
                tokenExpiresAt = LocalDateTime.now().plusSeconds(expiresIn - 50);
            }
        } else {
            log.info("APIGEE GET TOKEN IS NOT ENABLED ENV DEV");
        }

        return authorizationOauth2TokenPrefix + " " + token;
    }

    @Override
    public String refreshToken() {
        this.token = null;
        log.info("Refresh Token");
        return getToken();
    }

/*    @PostConstruct
    public void runAfter() {
        String token = getToken();
    }*/
}
