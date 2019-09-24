package org.kpmp.auth;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserPortalService {

    @Value("${userportal.url}")
    private String userPortalURL;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public UserAuth getUserAuth(String shib_id) {
        HttpHeaders headers = new HttpHeaders();
        // We will need to create a token for each application
        headers.set("X-API-TOKEN", "1ef4ecb8-2c2a-48af-889b-3ceb9ca9d102");
        HttpEntity<?> entity = new HttpEntity<Object>("body", headers);
        //UserAuth userAuth;
        RestTemplate restTemplate = new RestTemplate();
        try {
            //userAuth = restTemplate.getForObject(userPortalURL + "/api/user/" + shib_id, UserAuth.class);
            UserAuth userAuth = restTemplate.exchange(userPortalURL + "/api/user/" + shib_id,
                    HttpMethod.GET, entity, UserAuth.class).getBody();
            return userAuth;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
