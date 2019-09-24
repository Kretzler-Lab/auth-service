package org.kpmp.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import com.auth0.jwt.interfaces.DecodedJWT;

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
        //return userPortalService.getUserAuth(shibId);
        try {
            return userPortalService.getUserAuth(shibId);}
        catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }
    }

}
