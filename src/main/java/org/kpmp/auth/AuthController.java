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
        //Setting the userID to the session ID for now, but in the future this should probably come from a DB.
        user.setId(session.getId());
        session.setAttribute("user", user);
        return new RedirectView(redirectURL);
    }

    @CrossOrigin
    @RequestMapping(value = "/auth")
    public @ResponseBody AuthResponse getAuth(@RequestBody Map<String, Object> payload, HttpSession httpSession) throws IOException {
        AuthResponse auth = new AuthResponse();
        String token = (String) payload.get("token");
        session = httpSession;
        User user = new User();
        if (token != null) {
            try {
                DecodedJWT verifiedToken = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                        .build()
                        .verify(token);
                auth.setToken(verifiedToken.getToken());
                //In the future, grab the user from the DB based on the ID stored in the JWT subject.
                user.setId(verifiedToken.getSubject());
                auth.setUser(user);
            } catch (JWTVerificationException exception) {
                auth.setToken(null);
            }
        } else if (session != null) {
            user = (User) session.getAttribute("user");
            token = JWT.create().withSubject(user.getId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET.getBytes()));
            auth.setToken(token);
            auth.setUser((User) session.getAttribute("user"));
            session = null;
        }
        return auth;
    }

}
