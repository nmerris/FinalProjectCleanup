## [Please see my github wiki for this web app!](https://github.com/nmerris/Student-Bot-5K/wiki)
#### [Or go straight to the app live on Heroku](https://student-bot-5000.herokuapp.com/)

_Please note that Heroku can sometimes have an exceedingly slow response time at first, so if the welcome page doesn't load on the first try, it should on the second try.  After it 'wakes up' the pages should respond well._




#### Note to anyone deploying this app to Heroku

* you must create a postgresql database before pushing to heroku
* and you must change the pom and properties files to use postgresql instead of mysql
* you will need to modify the properties file to use a valid email account, password is empty here
* you must set the heroku server timezone or timestamps will be off

1. log in to Heroku, go to settings for app, got to Environment Variables
  - add TZ   America/New_York
  
2. do it straight from the command line
  - log in to heroku
  - type heroku config:add TZ="America/New_York"
