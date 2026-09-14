# SeramitaeLib

SeramitaeLib is a quality-of-life wrapper library for FTC SDK hardware. It keeps common robot code concise while allowing access to the underlying FTC SDK object when needed.

```java
Motor intake = new Motor(hardwareMap, "intake")
        .reverse()
        .brake()
        .setMaxPower(0.8);

intake.setPower(1);
```

Current Maven coordinate:

```text
org.seramitae:seramitaelib:0.1.0
```

The library compiles against FTC SDK 11.2.1. It is published to GitHub Packages; because the repository is private, consuming projects need GitHub Packages authentication. Configure credentials outside the repository, for example in your user Gradle properties file, and never commit a token.
