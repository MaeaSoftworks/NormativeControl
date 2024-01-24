# normative control core

## About

Backend application for Normative Control service.

By Maea Softworks.

## Building from sources & launch

_Launch method from examples will build app from sources and launch it with **in-memory 
database** and **in-memory S3 storage emulator** (standalone mode)._

**Windows:**  
```shell
./standalone.bat
```

**Linux:**  
Unfortunately, there is no automatic launch script.
But you can build it manually:
```shell
./gradlew build
```
Then you **must** set environment variable `NORMATIVECONTROL_PROFILE` to `"standalone"` and 
launch jar file as common:
```shell
java -jar api/build/libs/api-all.jar
```