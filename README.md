spray-heroku-example
====================

Spray Example App Deployable to Heroku
--------------------------------------

This is an example of a simple Rest API in Spray that can be deployed to Heroku with New Relic stats supplied by Kamon.

To run the tests use  `sbt test -Dconfig.resource=src/main/resources/dev.conf` as you need to use a different akka conf file.

To run the example locally use foreman. Create a .env file with the following variables defined. Add in a new relic licence key and app name. If you don''t have one add the new relic app in Heroku. You must have a newrelic.yml file in the project root, you can get this from the New Relic dashboard for your app.

Heroku config variables
-----------------------
```
AKKA_OPTS=-Dconfig.file=target/universal/stage/conf/application.conf
JAVA_OPTS=-Xmx384m -Xss512k -XX:+UseCompressedOops
NEW_RELIC_LICENSE_KEY=
NEW_RELIC_LOG=stdout
NEW_RELIC_APP_NAME=
PATH=.jdk/bin:.sbt_home/bin:/usr/local/bin:/usr/bin:/bin
REPO=/app/.sbt_home/.ivy2/cache
SBT_OPTS=-Xmx384m -Xss512k -XX:+UseCompressedOops
```

To run the example on Heroku

1. Create a Heroku instance and endpoint - 
2. Check the environment variables and add any required.
3. Push to Heroku. You should see Heroku picking up the scala project. 
4. You will see the Kamon reporter.


