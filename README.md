# Authentication Service for UM KPMP Apps

This is the repository for the authentication service for UM KPMP apps. 

# Setup

The /login endpoint should be the only location covered by Shibboleth, so the Apache config should look something like this:

```
  <Location "/api/login">
    AuthType shibboleth
    ShibRequireSession On
    ShibUseHeaders On
    Require valid-user
    Header set Cache-Control "no-store, max-age=0, private"
  </Location>
```

# Usage

## /login Endpoint

If the user does not have a JWT or a valid session, the /login endpoint should be called first to send them to Shibboleth. 
The /login endpoint needs to be called with the "redirect" parameter so it knows where to send the user after login:

`https://auth.kpmp.org/api/login?redirect=http://upload.kpmp.org`

Once the user authenticates via Shibboleth, the auth service starts a session containing the user information.

## /auth Endpoint

The /auth endpoint does three things: 
1) If the user has established a session, i.e. just authenticated via /login and Shibboleth, it responds with a JSON object containing a JWT token and user information and then destroys the session. 
```
{
    "token": "XXXXXXXXXXXXXXXX.XXXXXXXXXXXX.XXXXXXXXXX"
    "user": {
        "firstName": "Jane"
        "lastName": "Smith"
        "displayName": "J Smith"
        "email": "jsmith@myuniversity.org"
    }
}
```

2) If /auth is called with a JWT, the service checks the validity of JWT and either decodes the user information and sends back the auth response above or sends back the response above with a null token and user.
3) If /auth is called without a session or a JWT it returns a the response above with a null token and user. 

