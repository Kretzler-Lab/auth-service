package org.kpmp.auth;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import users.User;

@Controller
public class AuthController {

    private HttpSession session;
    private ShibbolethUserService userService;
    private UTF8Encoder encoder;
    private TokenService tokenService;

    @Autowired
    public AuthController(ShibbolethUserService userService, UTF8Encoder encoder, TokenService tokenService) {
        this.userService = userService;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody RedirectView login(HttpServletRequest request, HttpSession httpSession) throws UnsupportedEncodingException {
        String redirectURL = request.getParameter("redirect");
        session = httpSession;
        User user = userService.getUser(request, encoder);
        session.setAttribute("user", user);
        return new RedirectView(redirectURL);
    }

    @CrossOrigin
    @RequestMapping(value = "/auth")
    public @ResponseBody AuthResponse getAuth(@RequestBody Map<String, Object> payload) throws IOException {
        AuthResponse auth = new AuthResponse();
        String tokenString = (String) payload.get("token");
        if (tokenString != null) {
            DecodedJWT verifiedToken = tokenService.verifyToken(tokenString);
            if (verifiedToken != null) {
                auth.setToken(verifiedToken.getToken());
                auth.setUser(tokenService.getUserFromToken(verifiedToken));
            }
        }

        if (session != null && auth.getToken() == null) {
            User user = (User) session.getAttribute("user");
            tokenString = tokenService.buildTokenWithUser(user);
            auth.setToken(tokenString);
            auth.setUser(user);
            session = null;
        }

        return auth;
    }

}
