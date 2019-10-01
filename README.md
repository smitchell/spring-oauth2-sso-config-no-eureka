#Spring Cloud SSO Zuul Proxy w/o Config or Discovery

## How to run this project locally

### Start the Authentication Server
1) cd ./spring-security-zuul-sso/authentication-service
2) mvn spring-boot:run

### Start the Protected Web site
1) cd ./spring-security-zuul-sso/protected-web-site
2) ng serve --baseHref=/protected-web-site/ --port=9001

### Start the Zuul Proxy
1) /spring-security-zuul-sso/proxy-service
2) mvn spring-boot:run


## Validation

1. *Angular* - Open the Angular App directly: http://localhost:9001/protected-web-site/
2. *Authentication Server* - Get an access token from the authentication server (see curl command below)
3. *Proxy with SSO* - Access the Angular App via the proxy: http://localhost:9000/protected-web-site

```   
curl --user zuul-proxy-example:client-secret \
http://localhost:9002/oauth/token \
-d 'grant_type=password&client_id=zuul-proxy-example&username=user&password=password'
```

## Testing
1) User tries to access the protected web site via authentication SSO-enabled proxy server, http://localhost:9000/protected-web-site/
2) Spring Security on the proxy server redirects the user's browser to the Login page on the authentication server (http://localhost:9002/login).
3) The user signs in and posts the Login form to the authentication server.
4) The authentication server redirects the user to the login page on the proxy service passing a JSESSIONID (http://localhost:9000/login)
5) The proxy server login page bypasses the 3rd Party Permission page and redirects the browser to the protected resource URL (/protected-web-site/).
6) The http://localhost:9000/protected-web-site/ is displayed.
