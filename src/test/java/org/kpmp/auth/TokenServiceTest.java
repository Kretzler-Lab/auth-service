package org.kpmp.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import users.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class TokenServiceTest {

    private TokenService tokenService;
    @Mock
    private SecurityConstants securityConstants;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tokenService = new TokenService(securityConstants);
    }

    @After
    public void tearDown() throws Exception {
        tokenService = null;
    }

    @Test
    public void testBuildandVerifyToken() throws Exception {
        when(securityConstants.getSecret()).thenReturn("GiveMeTheInfinityStones".getBytes());
        User user = new User();
        user.setId("123");
        user.setDisplayName("Thanos the Great");
        user.setEmail("bigguy@titan.com");
        user.setFirstName("Thanos");
        user.setLastName("Smith");
        String token = tokenService.buildTokenWithUser(user);
        assertNotNull(token);
        DecodedJWT verifiedToken = tokenService.verifyToken(token);
        assertNotNull(verifiedToken);
        User userFromJWT = tokenService.getUserFromToken(verifiedToken);
        assertEquals(userFromJWT.getDisplayName(), user.getDisplayName());
        assertEquals(userFromJWT.getEmail(), user.getEmail());
        assertEquals(userFromJWT.getFirstName(), user.getFirstName());
        assertEquals(userFromJWT.getLastName(), user.getLastName());
        assertEquals(verifiedToken.getSubject(), user.getId());
    }

}
