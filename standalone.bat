set normativecontrol.profile=standalone

if not exist api\build.gradle.kts.bak (
    type api\build.gradle.kts > api\build.gradle.kts.bak
    type api\build.gradle.kts.bak | find /v /i "runtimeOnly(""org.komapper:komapper-dialect-postgresql-r2dbc"")" > api\build.gradle.kts
)

gradlew build -x test && java -jar api\build\libs\api-all.jar