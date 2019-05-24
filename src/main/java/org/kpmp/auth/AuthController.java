package org.kpmp.auth;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.kpmp.auth.SecurityConstants.EXPIRATION_TIME;
import static org.kpmp.auth.SecurityConstants.SECRET;

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

    @Autowired
    public AuthController(ShibbolethUserService userService, UTF8Encoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
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
        String token = (String) payload.get("token");
        User user;
        ObjectMapper mapper = new ObjectMapper();
        if (token != null) {
            try {
                DecodedJWT verifiedToken = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token);
                auth.setToken(verifiedToken.getToken());
                user = mapper.readValue(verifiedToken.getClaim("user").asString(), User.class);
                auth.setUser(user);
            } catch (JWTVerificationException exception) {
                auth.setToken(null);
            }
        }
        if (session != null && auth.getToken() == null) {
            user = (User) session.getAttribute("user");
            token = JWT.create().withSubject(user.getId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).withClaim("user", user.toJson())
                    .sign(HMAC512(SECRET.getBytes()));
            auth.setToken(token);
            auth.setUser((User) session.getAttribute("user"));
            session = null;
        }
        return auth;
    }

}
