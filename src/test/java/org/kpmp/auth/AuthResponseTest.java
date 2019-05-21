package org.kpmp.auth;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuthResponseTest {


    private AuthResponse authResponse;

    @Before
    public void setUp() throws Exception {
        authResponse = new AuthResponse();
    }

    @After
    public void tearDown() throws Exception {
        authResponse = null;
    }

    @Test
    public void testSetToken() throws Exception {
        authResponse.setToken("token");
        assertEquals("token", authResponse.getToken());
    }


}
