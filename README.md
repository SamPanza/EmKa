# EmKa

```shell
mvn -am -pl app compile exec:java@j
```

```shell
mvn -am -pl app compile exec:exec@e
```

Both unfriendly to `Ctrl+C`: `The build was canceled` & that's it

```shell
mvn -am -pl app package
java -jar app/target/emka-app-0-SNAPSHOT.jar
```
