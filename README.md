# SeramitaeLib

A lightweight and easy-to-use hardware library for **FIRST Tech Challenge (FTC)** robots.

SeramitaeLib provides wrappers around common FTC SDK hardware components to make robot code cleaner, easier to read, and faster to write.

## Features

SeramitaeLib currently includes:

* `Motor`
* `MotorEx`
* `ServoEx`
* `CRServoEx`
* `RGBLight`
* Motor encoder utilities
* Position control
* Velocity control
* Motor current monitoring
* Motor stall detection
* Power caching
* Servo caching
* Fluent APIs

---

# Installation

Add the GitHub Packages repository to your FTC project:

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Matteo0205/seramitaelib")

        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

Then add SeramitaeLib as a dependency:

```gradle
dependencies {
    implementation 'org.seramitae:seramitaelib:0.1.2'
}
```

Replace `0.1.2` with the version you want to use.

Your GitHub credentials can be stored in `gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Do not commit your GitHub token to a public repository.

---

# Motor

Package:

```java
import org.seramitae.ftc.hardware.Motor.Motor;
```

`Motor` is a wrapper around the FTC motor API that provides a simpler interface for controlling motors, encoders, position and velocity.

## Creating a Motor

```java
Motor motor = new Motor(
        hardwareMap,
        "motor"
);
```

You can also specify the motor's CPR and RPM:

```java
Motor motor = new Motor(
        hardwareMap,
        "motor",
        537.7,
        312
);
```

---

## Power Control

```java
motor.setPower(1);
```

Stop the motor:

```java
motor.stop();
```

You can also use:

```java
motor.set(0.5);
```

The behavior of `set()` depends on the selected `RunMode`.

---

## Maximum Power

Limit the maximum output:

```java
motor.setMaxPower(0.7);
```

Now:

```java
motor.setPower(1);
```

will only output:

```text
0.7
```

Read the current limit:

```java
double maxPower = motor.getMaxPower();
```

---

## Motor Direction

Reverse:

```java
motor.reverse();
```

Forward:

```java
motor.forward();
```

Or:

```java
motor.setInverted(true);
```

Check direction:

```java
boolean inverted = motor.isInverted();
```

---

## Zero Power Behavior

Brake:

```java
motor.brake();
```

Float/coast:

```java
motor.coast();
```

---

# Motor Run Modes

SeramitaeLib provides three high-level motor control modes:

```java
Motor.RunMode.RawPower
Motor.RunMode.VelocityControl
Motor.RunMode.PositionControl
```

Set one using:

```java
motor.setRunMode(
        Motor.RunMode.RawPower
);
```

---

## Raw Power

```java
motor.setRunMode(
        Motor.RunMode.RawPower
);

motor.set(0.8);
```

In `RawPower`, `set()` directly controls motor power.

---

# Position Control

Switch to position control:

```java
motor.setRunMode(
        Motor.RunMode.PositionControl
);
```

Configure the controller:

```java
motor.setPositionCoefficient(0.005);
motor.setPositionTolerance(15);
```

Set a target:

```java
motor.setTargetPosition(1200);
```

Then update the motor:

```java
motor.set(0.8);
```

The value passed to `set()` represents the maximum output allowed for the position controller.

Check whether the target has been reached:

```java
if (motor.atTargetPosition()) {
    motor.stop();
}
```

---

# Velocity Control

Switch to velocity control:

```java
motor.setRunMode(
        Motor.RunMode.VelocityControl
);
```

Configure PID:

```java
motor.setVeloCoefficients(
        0.01,
        0,
        0
);
```

Configure feedforward:

```java
motor.setFeedforwardCoefficients(
        0.05,
        0.0004
);
```

Then:

```java
motor.set(0.5);
```

represents 50% of the motor's calculated maximum velocity.

---

# Encoder

Each `Motor` contains an encoder utility:

```java
motor.encoder
```

Get the encoder position:

```java
int position =
        motor.getCurrentPosition();
```

or:

```java
int position =
        motor.encoder.getPosition();
```

Get velocity:

```java
double velocity =
        motor.getVelocity();
```

Get corrected velocity:

```java
double velocity =
        motor.getCorrectedVelocity();
```

---

## Software Encoder Reset

```java
motor.resetEncoder();
```

This uses a software offset rather than resetting the physical motor controller encoder.

The motor can therefore continue operating without changing its FTC SDK run mode.

For a full hardware reset:

```java
motor.stopAndResetEncoder();
```

---

## Encoder Revolutions

When CPR is configured:

```java
double revolutions =
        motor.encoder.getRevolutions();
```

---

## Distance Per Pulse

Configure how much physical distance corresponds to one encoder tick:

```java
motor.setDistancePerPulse(0.01);
```

Then:

```java
double distance =
        motor.getDistance();
```

You can also set a position target using physical distance:

```java
motor.setTargetDistance(50);
```

---

# MotorEx

Package:

```java
import org.seramitae.ftc.hardware.Motor.MotorEx;
```

`MotorEx` extends `Motor` and provides additional features for `DcMotorEx`.

These include:

* Direct velocity control
* Power caching
* Current sensing
* Current alerts
* Stall detection

Create one:

```java
MotorEx motor = new MotorEx(
        hardwareMap,
        "motor"
);
```

Or with CPR and RPM:

```java
MotorEx motor = new MotorEx(
        hardwareMap,
        "motor",
        537.7,
        312
);
```

---

# MotorEx Velocity

Set velocity directly in ticks per second:

```java
motor.setVelocity(1400);
```

Read velocity:

```java
double velocity =
        motor.getVelocity();
```

Angular units are also supported:

```java
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
```

Example:

```java
motor.setVelocity(
        100,
        AngleUnit.RADIANS
);
```

Read it:

```java
double velocity =
        motor.getVelocity(
                AngleUnit.RADIANS
        );
```

---

# Motor Power Caching

`MotorEx` avoids unnecessary hardware writes when the requested power has not changed enough.

Configure the tolerance:

```java
motor.setCachingTolerance(0.01);
```

For example:

```java
motor.setPower(0.7);
motor.setPower(0.7);
motor.setPower(0.7);
```

does not need to repeatedly send the same value to the motor controller.

Clear the cache:

```java
motor.clearCache();
```

Force a hardware write:

```java
motor.forceSetPower(0.7);
```

---

# Current Monitoring

Get current consumption in amps:

```java
double amps =
        motor.getCurrent();
```

Or specify the unit:

```java
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
```

```java
double milliamps =
        motor.getCurrent(
                CurrentUnit.MILLIAMPS
        );
```

---

## Current Alerts

Configure an alert:

```java
motor.setCurrentAlert(
        5,
        CurrentUnit.AMPS
);
```

Check it:

```java
if (motor.isOverCurrent()) {
    motor.stop();
}
```

---

# Stall Detection

A motor can be considered stalled when it has high power but very little velocity.

Example:

```java
if (motor.isStalled(
        0.8,
        50
)) {
    motor.stop();
}
```

You can additionally require high current:

```java
if (motor.isStalled(
        0.8,
        50,
        4.5
)) {
    motor.stop();
}
```

This checks:

```text
High Power
    +
Low Velocity
    +
High Current
    =
Possible Stall
```

---

# ServoEx

Package:

```java
import org.seramitae.ftc.hardware.Servo.ServoEx;
```

Create a servo:

```java
ServoEx claw = new ServoEx(
        hardwareMap,
        "claw"
);
```

You can define its angular range:

```java
ServoEx claw = new ServoEx(
        hardwareMap,
        "claw",
        0,
        180
);
```

---

# Servo Position

```java
claw.setPosition(0.5);
```

Read position:

```java
double position =
        claw.getPosition();
```

Move relative to the current position:

```java
claw.rotateBy(0.1);
```

---

# Servo Angles

Move to an angle:

```java
claw.turnToAngle(90);
```

Move relative to the current angle:

```java
claw.rotateByAngle(20);
```

Read the angle:

```java
double angle =
        claw.getAngle();
```

Radians are also supported:

```java
claw.turnToAngle(
        Math.PI / 2,
        AngleUnit.RADIANS
);
```

---

# Servo Range

Set the physical angle range:

```java
claw.setRange(
        20,
        160
);
```

Read it:

```java
double range =
        claw.getAngleRange();
```

---

# Servo Inversion

```java
claw.setInverted(true);
```

Toggle it:

```java
claw.toggleInverted();
```

Check it:

```java
boolean inverted =
        claw.getInverted();
```

---

# Servo Caching

```java
claw.setCachingTolerance(0.01);
```

Repeated commands that are inside the tolerance are not sent unnecessarily.

Clear the cache:

```java
claw.clearCache();
```

Force a hardware write:

```java
claw.forceSetPosition(0.5);
```

---

# Servo PWM Control

When supported by the underlying servo controller:

```java
claw.setPwmRange(
        500,
        2500
);
```

Disable PWM:

```java
claw.disablePwm();
```

Enable PWM:

```java
claw.enablePwm();
```

Check:

```java
boolean enabled =
        claw.isPwmEnabled();
```

---

# CRServoEx

Package:

```java
import org.seramitae.ftc.hardware.Servo.CRServoEx;
```

`CRServoEx` provides a simpler API for FTC continuous rotation servos.

Create one:

```java
CRServoEx intake = new CRServoEx(
        hardwareMap,
        "intake"
);
```

---

## Power

```java
intake.setPower(1);
```

Reverse:

```java
intake.setPower(-1);
```

Stop:

```java
intake.stop();
```

Convenience methods are also available:

```java
intake.forward();

intake.reverse();

intake.stop();
```

---

## CRServo Inversion

```java
intake.setInverted(true);
```

Check:

```java
boolean inverted =
        intake.getInverted();
```

Toggle:

```java
intake.toggleInverted();
```

---

## CRServo Caching

Configure caching:

```java
intake.setCachingTolerance(0.01);
```

Clear it:

```java
intake.clearCache();
```

Force a write:

```java
intake.forceSetPower(1);
```

---

# RGBLight

Package:

```java
import org.seramitae.ftc.hardware.Lights.RGBLight;
```

`RGBLight` provides a simple interface for servo-controlled RGB lights.

Create one:

```java
RGBLight light = new RGBLight(
        hardwareMap,
        "rgb"
);
```

---

# Colors

Built-in colors include:

```text
OFF
RED
ORANGE
YELLOW
SAGE
GREEN
AZURE
BLUE
INDIGO
VIOLET
WHITE
```

Use convenience methods:

```java
light.red();

light.orange();

light.yellow();

light.green();

light.blue();

light.violet();

light.white();

light.off();
```

Or use the color enum:

```java
light.setColor(
        RGBLight.Color.RED
);
```

For example:

```java
if (motor.isOverCurrent()) {
    light.red();
} else {
    light.green();
}
```

---

# RGB Range

Set the raw light controller position:

```java
light.setPosition(0.5);
```

Or use a percentage:

```java
light.setColorFromRange(50);
```

Values are expected between:

```text
0 - 100
```

---

# RGBLight Caching

Configure tolerance:

```java
light.setCachingTolerance(0.001);
```

Clear the cache:

```java
light.clearCache();
```

Force a hardware write:

```java
light.forceSetPosition(0.5);
```

---

# Fluent API

Most SeramitaeLib hardware methods return their own object, allowing method chaining.

For example:

```java
MotorEx shooter = new MotorEx(
        hardwareMap,
        "shooter"
)
        .setMaxPower(1)
        .brake()
        .setCachingTolerance(0.01);
```

Servo example:

```java
ServoEx claw = new ServoEx(
        hardwareMap,
        "claw",
        0,
        180
);

claw
        .setCachingTolerance(0.01)
        .turnToAngle(90);
```

RGB example:

```java
RGBLight status = new RGBLight(
        hardwareMap,
        "status"
)
        .setCachingTolerance(0.001)
        .blue();
```

---

# Complete FTC Example

```java
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.seramitae.ftc.hardware.Lights.RGBLight;
import org.seramitae.ftc.hardware.Motor.MotorEx;
import org.seramitae.ftc.hardware.Servo.ServoEx;

@TeleOp(name = "SeramitaeLib Example")
public class SeramitaeExample extends OpMode {

    private MotorEx intake;
    private ServoEx claw;
    private RGBLight status;

    @Override
    public void init() {

        intake = new MotorEx(
                hardwareMap,
                "intake"
        );

        claw = new ServoEx(
                hardwareMap,
                "claw",
                0,
                180
        );

        status = new RGBLight(
                hardwareMap,
                "status"
        );

        intake
                .brake()
                .setCachingTolerance(0.01);

        claw.setCachingTolerance(0.01);

        status.blue();
    }

    @Override
    public void start() {
        status.green();
    }

    @Override
    public void loop() {

        if (gamepad1.a) {
            intake.setPower(1);
        } else if (gamepad1.b) {
            intake.setPower(-1);
        } else {
            intake.stop();
        }

        if (gamepad1.left_bumper) {
            claw.turnToAngle(0);
        }

        if (gamepad1.right_bumper) {
            claw.turnToAngle(90);
        }

        if (intake.isStalled(
                0.8,
                50,
                4
        )) {
            intake.stop();
            status.red();
        } else {
            status.green();
        }

        telemetry.addData(
                "Intake Velocity",
                intake.getVelocity()
        );

        telemetry.addData(
                "Intake Current",
                intake.getCurrent()
        );

        telemetry.addData(
                "Claw Angle",
                claw.getAngle()
        );

        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stop();
        status.off();
    }
}
```

---

# Raw Hardware Access

If you need functionality that is not directly exposed by SeramitaeLib, you can still access the underlying FTC SDK hardware.

Motor:

```java
motor.raw();
```

Servo:

```java
servo.getServo();
```

CRServo:

```java
crServo.getServo();
```

RGBLight:

```java
light.raw();
```

---

# Project Structure

The hardware package currently contains:

```text
org.seramitae.ftc.hardware
│
├── Motor
│   ├── Motor.java
│   └── MotorEx.java
│
├── Servo
│   ├── ServoEx.java
│   └── CRServoEx.java
│
└── Lights
    └── RGBLight.java
```

---

# Goals

SeramitaeLib aims to provide:

* Cleaner FTC robot code
* Less repetitive hardware code
* Easy-to-use hardware wrappers
* Better hardware write efficiency
* Useful motor safety utilities
* Simple encoder utilities
* Readable APIs
* Compatibility with the FTC SDK

The library is designed so you can use as much or as little of it as you want while still having access to the original FTC SDK hardware objects.

---

# License

Add the license used by your project here.

---

# Contributing

Contributions, bug reports and feature suggestions are welcome.

If you find an issue, open an issue in the repository with:

* The problem
* FTC SDK version
* SeramitaeLib version
* Relevant code
* Error logs when applicable
## AI Disclaimer

This README was created with the assistance of artificial intelligence (AI). AI was used to help organize the documentation, explain the library's features, and generate usage examples based on the SeramitaeLib source code. While the documentation has been prepared to be as accurate as possible, some examples or descriptions may contain errors or may not reflect the latest version of the library. Always refer to the source code as the definitive reference for SeramitaeLib's behavior and API.
