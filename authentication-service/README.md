*HOW TO RUN THIS PROJECT*

mvn spring-boot:run

*HOW TO TEST*

curl --user proxy-client:client-secret \
     http://localhost:9002/oauth/token \
     -d 'grant_type=password&client_id=proxy-client&username=user&password=password'
