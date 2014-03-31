spray-heroku-example
====================

Spray Example App Deployable to Heroku with New Relic Stats supplied by Kamon
--------------------------------------

This is an example of a simple Rest API in Spray that can be deployed to [Heroku](heroku.com) with [New Relic](newrelic.com) stats supplied by [Kamon](kamon.io).

To run the tests use  `sbt test -Dconfig.resource=src/main/resources/dev.conf` as you need to use a test version of the akka conf file.

To run the example locally use [foreman](https://github.com/ddollar/foreman). Create a .env file with the following variables defined - these replicate the Heroku environment locally.

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
Add in your New Relic licence key and app name. Download your newrelic.yml file from your app dashboard on NewRelic and add it to your repo.

To start the app use `foreman start`. You can test the app is working by using 
```curl http://localhost:5000/search?search=hello```
If you want to test the authentication add ```AUTH=yourauthkey``` to the .env file. Now run 
```curl -u yourauthkey:yourauthkey http://localhost:5000/search?search=hello```

To run the app on Heroku, first create a new Heroku app, either on the command line or on Heroku's website. Add the NewRelic add-on to your app. Go to the NewRelic dashboard page by clicking on the NewRelic link on the Heroku app dashboard. Here you can download your newrelic.yml file and get the newrelic license key. Heroku should add these environment variables automatically. You can check the Heroku environment by running 
```heroku config```
You will need to add an auth setting for the app's authentication by running 
```heroku config:set AUTH=xxx```
, where xxx is the basic auth key you want to use, and
```heroku config:set AKKA_OPTS=-Dconfig.file=target/universal/stage/conf/application.conf```
to set the Akka conf file

Heroku config variables
-----------------------
```
AUTH=
AKKA_OPTS=-Dconfig.file=target/universal/stage/conf/application.conf
JAVA_OPTS=-Xmx384m -Xss512k -XX:+UseCompressedOops
NEW_RELIC_LICENSE_KEY=
NEW_RELIC_LOG=stdout
NEW_RELIC_APP_NAME=
PATH=.jdk/bin:.sbt_home/bin:/usr/local/bin:/usr/bin:/bin
REPO=/app/.sbt_home/.ivy2/cache
SBT_OPTS=-Xmx384m -Xss512k -XX:+UseCompressedOops
```
To run the app you deploy using `git push heroku master`. You should see Heroku detect the Scala code, build the app and execute it. You can check the newrelic stats for each of the endpoints.


