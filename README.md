# luminor-imdb-task
test task for luminor

dependencies:
allure-2.39.0
java 21

java libraries used:
testng:7.12.0,
selenide:7.16.0,
allure-testng:2.34.0.

running tests and generating the reports:
```
cd to the luminor-imdb-task folder
./gradlew test
allure serve build/allure-results
```
I've also provided an allure report of my own (allure-report.zip). To run it:

```
1. unzip the file

allure open allure-report
```