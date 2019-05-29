package org.kpmp.auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecurityConstantsTest {

    private SecurityConstants securityConstants;

    @Before
    public void setUp() throws Exception {
        securityConstants = new SecurityConstants();
    }

    @After
    public void tearDown() throws Exception {
        securityConstants = null;
    }

    @Test
    public void testGetSecret() {
        assertEquals(securityConstants.getSecret().length, 32);
    }

}
