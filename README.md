# Student Bot 5K

description goes here

This readme is under construction!

To see the app live on heroku:

put heroku url here

TO LOG IN:
admin: FILL ME IN
student: create your own or log in with FILL ME IN
teacher: create your own or log in with FILL ME IN




!!!! IMPORTANT !!!!
MUST SET THE HEROKU SERVER TIMEZONE - OR TIMESTAMPS WILL BE OFF
two ways to do this:
1. log in to Heroku, go to settings for app, got to Environment Variables
  - add TZ   America/New_York
  
  OR
  
2. do it straight from the command line
  - log in
  - type heroku config:add TZ="America/New_York"

-- authorites (these are automatically generated every time the app runs)
INSERT INTO role VALUES(1, 'ADMIN');
INSERT INTO role VALUES(2, 'STUDENT');
INSERT INTO role VALUES(3, 'TEACHER');
