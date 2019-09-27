package org.kpmp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;


import static org.springframework.http.HttpStatus.*;

@Controller
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private UserPortalService userPortalService;

    @Autowired
    public AuthController(UserPortalService userPortalService) {
        this.userPortalService = userPortalService;
    }

    @RequestMapping(value = "/v1/user/info/{shibId}", method = RequestMethod.GET)
    public @ResponseBody UserAuth getUserInfo(@PathVariable("shibId") String shibId) {
        try {
            return userPortalService.getUserAuth(shibId);}
        catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(NOT_FOUND)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            } else {
                throw new ResponseStatusException(e.getStatusCode(), "There was a problem connecting to the User Portal");
            }
        }
    }

    @RequestMapping(value = "/v1/user/info/{clientId}/{shibId}", method = RequestMethod.GET)
    public @ResponseBody UserAuth getUserInfoWithClient(@PathVariable("clientId") String clientId, @PathVariable("shibId") String shibId) {
        try {
            return userPortalService.getUserAuthWithClient(clientId, shibId);}
        catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(NOT_FOUND)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            } else {
                throw new ResponseStatusException(e.getStatusCode(), "There was a problem connecting to the User Portal");
            }
        }
    }


}
