set normativecontrol.profile=standalone
gradlew build -x test
java -jar api/build/libs/api-all.jar