# SeramitaeLib

A Java library for FIRST Tech Challenge (FTC) robots, providing hardware wrappers, motor and servo groups, feedback controllers, feedforward utilities, mecanum drivetrain control, Kalman filtering, and motion profiling.

SeramitaeLib is designed to simplify robot programming, reduce repetitive hardware code, and provide reusable components for FTC projects.

## Features

- Motor and servo hardware wrappers
- Motor, servo, and continuous-rotation servo groups
- PID and other feedback controllers
- Feedforward utilities for motors, arms, and elevators
- Field-centric and robot-centric mecanum drivetrain
- One-dimensional Kalman filter
- Trapezoidal and triangular motion profiles
- Compatibility with the FTC SDK

## Installation

SeramitaeLib is distributed through GitHub Packages.

### 1. Add the Maven repository

Add the repository to your FTC project's Gradle configuration:

```gradle
maven {
    url = uri("https://maven.pkg.github.com/Matteo0205/seramitaelib")

    credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
    }
}
```

GitHub Packages requires authentication. Configure `GITHUB_ACTOR` and `GITHUB_TOKEN` with credentials that have permission to read the package.

### 2. Add the dependency

In your FTC project's `TeamCode/build.gradle`:

```gradle
dependencies {
    implementation 'org.seramitae:seramitaelib:VERSION'
}
```

Replace `VERSION` with the version you have published to GitHub Packages.

Sync Gradle after adding the dependency.

## Project Structure

```text
org.seramitae.ftc
│
├── hardware
│   ├── Motor
│   │   ├── Motor.java
│   │   ├── MotorEx.java
│   │   └── MotorGroup.java
│   │
│   ├── Servo
│   │   ├── ServoEx.java
│   │   ├── ServoGroup.java
│   │   ├── CRServoEx.java
│   │   └── CRServoGroup.java
│   │
│   └── Lights
│       └── RGBLight.java
│
├── controller
│   ├── Controller.java
│   ├── PController.java
│   ├── PDController.java
│   ├── PIDController.java
│   ├── PIDFController.java
│   ├── SquIDController.java
│   ├── SquIDFController.java
│   │
│   └── feedforward
│       ├── SimpleMotorFeedforward.java
│       ├── ArmFeedforward.java
│       └── ElevatorFeedforward.java
│
├── drive
│   └── MecanumDrive.java
│
├── filter
│   └── KalmanFilter.java
│
└── motion
    └── MotionProfile.java
```

# Hardware

## Motor

Package: `org.seramitae.ftc.hardware.Motor`

`Motor` is a wrapper around the FTC SDK's `DcMotorEx` interface. It simplifies motor initialization, direction, braking, power control, encoder access, and supported control modes.

### Basic usage

```java
import org.seramitae.ftc.hardware.Motor.Motor;

Motor motor = new Motor(
        hardwareMap,
        "intake"
);

motor.forward();
motor.brake();

motor.setPower(1.0);
```

Stop the motor:

```java
motor.stop();
```

Reverse its direction:

```java
motor.reverse();
```

Set the zero-power behavior:

```java
motor.brake();
```

Or:

```java
motor.coast();
```

### Run modes

The wrapper provides the following run modes:

| Mode | Purpose |
|---|---|
| `RawPower` | Direct motor power control |
| `VelocityControl` | Velocity-based control |
| `PositionControl` | Position-based control |

Example:

```java
motor.setRunMode(
        Motor.RunMode.RawPower
);
```

### Encoder

The motor wrapper also provides encoder-related functionality for reading position and velocity and resetting encoder measurements.

## MotorEx

`MotorEx` extends the basic motor wrapper with additional functionality, including direct velocity access, motor current monitoring, power caching, and stall detection.

### Example

```java
import org.seramitae.ftc.hardware.Motor.MotorEx;

MotorEx motor = new MotorEx(
        hardwareMap,
        "intake",
        103.8,
        1620
);

motor.forward();
motor.brake();

motor.setPower(1.0);
```

Read motor velocity:

```java
double velocity = motor.getVelocity();
```

Read motor current:

```java
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

double current = motor.getCurrent(
        CurrentUnit.AMPS
);
```

Detect a stalled motor using the configured stall-detection parameters:

```java
boolean stalled = motor.isStalled(1, 4);
```

## MotorGroup

`MotorGroup` is intended for controlling multiple motors as a single mechanism.

Typical applications include:

- Dual-motor shooters
- Linear slides
- Multi-motor intakes
- Other synchronized motor mechanisms

The group provides shared power, direction, braking, and supported control operations, as well as access to individual members.

## ServoEx

Package: `org.seramitae.ftc.hardware.Servo`

`ServoEx` provides a convenient interface for FTC positional servos.

### Example

```java
import org.seramitae.ftc.hardware.Servo.ServoEx;

ServoEx servo = new ServoEx(
        hardwareMap,
        "servo"
);

servo.setPosition(0.5);
```

Move the servo to another position:

```java
servo.setPosition(1.0);
```

`ServoEx` can also be used with the library's motion profile to generate gradual position commands.

## ServoGroup

`ServoGroup` is designed to control multiple positional servos together.

It can be used for mechanisms with two or more servos that should receive coordinated position commands.

## CRServoEx

`CRServoEx` provides a wrapper for continuous-rotation servos.

Unlike a positional servo, a continuous-rotation servo is controlled using power rather than a target position.

### Example

```java
import org.seramitae.ftc.hardware.Servo.CRServoEx;

CRServoEx servo = new CRServoEx(
        hardwareMap,
        "spindexer"
);

servo.setPower(1.0);
```

Stop the servo:

```java
servo.stop();
```

Reverse its rotation:

```java
servo.setPower(-1.0);
```

## CRServoGroup

`CRServoGroup` allows multiple continuous-rotation servos to be controlled as one group.

This is useful for mechanisms that require multiple continuous-rotation servos to operate together.

## RGBLight

Package: `org.seramitae.ftc.hardware.Lights`

`RGBLight` is a wrapper for servo-controlled RGB indicators.

### Example

```java
import org.seramitae.ftc.hardware.Lights.RGBLight;

RGBLight light = new RGBLight(
        hardwareMap,
        "light"
);

light.green();
```

Change the color:

```java
light.blue();
```

Turn the light off:

```java
light.off();
```

### Color positions

| Color | Servo position |
|---|---:|
| Red | 0.279 |
| Orange | 0.333 |
| Yellow | 0.388 |
| Sage | 0.444 |
| Green | 0.500 |
| Azure | 0.555 |
| Blue | 0.611 |
| Indigo | 0.666 |
| Violet | 0.722 |

Actual colors depend on the connected indicator hardware.

# Controllers

Package: `org.seramitae.ftc.controller`

SeramitaeLib includes several feedback controllers for robot mechanisms.

| Controller | Description |
|---|---|
| `PController` | Proportional feedback |
| `PDController` | Proportional and derivative feedback |
| `PIDController` | Proportional, integral, and derivative feedback |
| `PIDFController` | PID with an additional feedforward term |
| `SquIDController` | Square-root-based error feedback |
| `SquIDFController` | Square-root-based feedback with an additional feedforward term |

## PIDController

A PID controller uses three terms to calculate a control output:

- Proportional (`kP`): responds to the current error.
- Integral (`kI`): responds to accumulated error.
- Derivative (`kD`): responds to the rate of change of the error.

### Example

```java
import org.seramitae.ftc.controller.PIDController;

PIDController controller = new PIDController(
        0.01,
        0.0,
        0.0
);

controller.setSetPoint(1000);
```

Inside the OpMode loop:

```java
double output = controller.calculate(
        motor.getVelocity()
);

motor.setPower(
        Math.max(-1.0, Math.min(1.0, output))
);
```

The controller must be updated regularly while the mechanism is operating.

### Changing coefficients

```java
controller.setPID(
        0.01,
        0.001,
        0.0
);
```

### Resetting

```java
controller.reset();
```

Resetting the controller is useful when stopping a mechanism or starting a new control operation.

# Feedforward

Package: `org.seramitae.ftc.controller.feedforward`

Feedforward estimates the control effort required to produce a desired movement.

It can be combined with feedback control to improve mechanism response.

## SimpleMotorFeedforward

Designed for motor velocity and acceleration control.

The model is:

`output = kS × sign(velocity) + kV × velocity + kA × acceleration`

Where:

| Parameter | Description |
|---|---|
| `kS` | Static friction compensation |
| `kV` | Velocity coefficient |
| `kA` | Acceleration coefficient |

## ArmFeedforward

Designed for rotating arm mechanisms.

In addition to velocity and acceleration, it includes a gravity-compensation term based on arm angle.

## ElevatorFeedforward

Designed for vertically moving mechanisms, such as linear slides.

It includes gravity compensation in addition to the other feedforward terms.

Feedforward parameters must be tuned for the actual mechanism.

# MecanumDrive

Package: `org.seramitae.ftc.drive`

`MecanumDrive` provides robot-centric and field-centric mecanum drivetrain control.

### Features

- Robot-centric driving
- Field-centric driving using the REV Hub IMU
- Runtime drive mode switching
- Heading reset
- Configurable speed multiplier
- Brake and coast modes
- Individual motor access

## Initialization

```java
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.seramitae.ftc.drive.MecanumDrive;
import org.seramitae.ftc.hardware.Motor.MotorEx;

MotorEx frontLeft = new MotorEx(
        hardwareMap,
        "frontLeft"
);

MotorEx frontRight = new MotorEx(
        hardwareMap,
        "frontRight"
);

MotorEx backLeft = new MotorEx(
        hardwareMap,
        "backLeft"
);

MotorEx backRight = new MotorEx(
        hardwareMap,
        "backRight"
);

frontLeft.setInverted(true);
backLeft.setInverted(true);

MecanumDrive drive = new MecanumDrive(
        frontLeft,
        frontRight,
        backLeft,
        backRight,
        hardwareMap,
        "imu"
);

drive.initializeIMU(
        RevHubOrientationOnRobot.LogoFacingDirection.UP,
        RevHubOrientationOnRobot.UsbFacingDirection.RIGHT
);

drive.brake();
drive.fieldCentric();
```

The motor directions and IMU orientation in this example must match your robot's physical configuration.

## Driving

```java
drive.drive(
        -gamepad1.left_stick_y,
        gamepad1.left_stick_x,
        gamepad1.right_stick_x
);
```

The `drive()` method uses the currently selected drive mode.

## Field-centric driving

```java
drive.fieldCentric();
```

In field-centric mode, translation commands are interpreted relative to the field rather than the robot's current heading.

An initialized IMU is required.

## Robot-centric driving

```java
drive.robotCentric();
```

In robot-centric mode, translation commands are interpreted relative to the robot.

## Toggle drive mode

```java
drive.toggleDriveMode();
```

Call this method once per button press, rather than continuously while a button is held.

## Reset heading

```java
drive.resetHeading();
```

This sets the current orientation as the new zero heading for the drivetrain.

## Speed multiplier

```java
drive.setSpeedMultiplier(0.4);
```

Restore full speed:

```java
drive.setSpeedMultiplier(1.0);
```

# KalmanFilter

Package: `org.seramitae.ftc.filter`

`KalmanFilter` implements a one-dimensional Kalman filter.

It combines a predicted state with a measurement to estimate a value while accounting for uncertainty.

Typical applications include filtering motor velocity, distance measurements, and individual position estimates.

## Initialization

```java
import org.seramitae.ftc.filter.KalmanFilter;

KalmanFilter filter = new KalmanFilter(
        0.1,
        4.0
);
```

The constructor takes:

| Parameter | Description |
|---|---|
| `processVariance` (`Q`) | Uncertainty added during prediction |
| `measurementVariance` (`R`) | Uncertainty associated with measurements |

## Filtering measurements

```java
double rawVelocity = motor.getVelocity();

double filteredVelocity = filter.update(
        rawVelocity
);
```

Call `update()` whenever a new measurement is available.

## Prediction and correction

The filter also supports an externally calculated state change.

```java
filter.predict(
        predictedPositionChange
);

double estimatedPosition = filter.correct(
        measuredPosition
);
```

Both operations can be combined:

```java
double estimatedPosition = filter.update(
        measuredPosition,
        predictedPositionChange
);
```

The predicted change must represent the estimated actual change in the state, not merely the desired movement.

## Resetting

```java
filter.reset(
        0.0,
        1.0
);
```

## Available methods

| Method | Purpose |
|---|---|
| `predict(stateChange)` | Predict the next state |
| `correct(measurement)` | Correct the estimate |
| `update(measurement)` | Predict and correct |
| `update(measurement, stateChange)` | Predict with a supplied change, then correct |
| `reset(estimate, covariance)` | Reset the filter |
| `getEstimate()` | Get the estimated state |
| `getCovariance()` | Get the current uncertainty |
| `getGain()` | Get the most recent Kalman gain |
| `setProcessVariance(value)` | Change process variance |
| `setMeasurementVariance(value)` | Change measurement variance |

This is a scalar Kalman filter. It does not implement full multidimensional robot pose estimation.

# MotionProfile

Package: `org.seramitae.ftc.motion`

`MotionProfile` generates a one-dimensional trajectory with configurable maximum velocity, acceleration, and deceleration.

It supports trapezoidal profiles and automatically generates triangular profiles when the requested movement is too short to reach maximum velocity.

## Initialization

```java
import org.seramitae.ftc.motion.MotionProfile;

MotionProfile profile = new MotionProfile(
        0.0,
        1200.0,
        800.0,
        1600.0,
        1200.0
);
```

Constructor parameters:

| Parameter | Description |
|---|---|
| `start` | Starting position |
| `goal` | Target position |
| `maxVelocity` | Maximum velocity |
| `maxAcceleration` | Maximum acceleration |
| `maxDeceleration` | Maximum deceleration |

For equal acceleration and deceleration, use the four-argument constructor:

```java
MotionProfile profile = new MotionProfile(
        0.0,
        1200.0,
        800.0,
        1600.0
);
```

## Reading the profile

```java
MotionProfile.State state =
        profile.calculate(timeSeconds);

double position = state.position;
double velocity = state.velocity;
double acceleration = state.acceleration;
```

Time is expressed in seconds.

## Checking completion

```java
if (profile.isFinished(timeSeconds)) {
    // Profile completed
}
```

The motion profile generates reference values. It does not directly control the motor or verify that the mechanism reached its destination.

# MotionProfile — Complete Servo Example

This example uses `MotionProfile` and `ServoEx` to move a positional servo smoothly between `0.0` and `1.0`.

Press **A** to move to `1.0` and **B** to return to `0.0`.

A new profile starts from the last commanded position whenever a button is pressed.

## MotionProfileServoTest.java

```java
package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.seramitae.ftc.hardware.Servo.ServoEx;
import org.seramitae.ftc.motion.MotionProfile;

@TeleOp(
        name = "Motion Profile Servo",
        group = "Test"
)
public class MotionProfileServoTest extends OpMode {

    private ServoEx servo;

    private MotionProfile profile;
    private ElapsedTime timer;

    private double currentPosition = 0.0;

    public static double MAX_VELOCITY = 0.5;
    public static double MAX_ACCELERATION = 1.0;
    public static double MAX_DECELERATION = 1.0;

    @Override
    public void init() {

        servo = new ServoEx(
                hardwareMap,
                "servo"
        );

        servo.setPosition(0.0);

        timer = new ElapsedTime();
    }

    @Override
    public void loop() {

        if (gamepad1.aWasPressed()) {
            moveTo(1.0);
        }

        if (gamepad1.bWasPressed()) {
            moveTo(0.0);
        }

        if (profile != null) {

            double elapsed = timer.seconds();

            MotionProfile.State state =
                    profile.calculate(elapsed);

            currentPosition = state.position;

            servo.setPosition(currentPosition);

            if (profile.isFinished(elapsed)) {
                profile = null;
            }
        }

        telemetry.addData(
                "Servo Position",
                currentPosition
        );

        telemetry.addData(
                "Profile Active",
                profile != null
        );

        telemetry.update();
    }

    private void moveTo(double target) {

        profile = new MotionProfile(
                currentPosition,
                target,
                MAX_VELOCITY,
                MAX_ACCELERATION,
                MAX_DECELERATION
        );

        timer.reset();
    }
}
```

## Servo profile configuration

| Parameter | Default | Description |
|---|---:|---|
| `MAX_VELOCITY` | 0.5 | Maximum commanded position change per second |
| `MAX_ACCELERATION` | 1.0 | Maximum commanded acceleration |
| `MAX_DECELERATION` | 1.0 | Maximum commanded deceleration |

With these values, a full movement from `0.0` to `1.0` takes approximately 2.5 seconds.

## How the example works

1. A button press creates a new motion profile.
2. The timer starts from zero.
3. Each OpMode loop calculates the profile's desired position.
4. `ServoEx` receives the new position.
5. Once the profile finishes, the servo remains at the final commanded position.

A standard positional servo has its own internal controller. This example controls the sequence of commanded positions rather than measuring or directly controlling the servo's physical velocity.

When the destination changes during movement, the example starts a new rest-to-rest profile. Because the new profile assumes zero initial velocity, the commanded velocity may change abruptly.

# Combining MotionProfile and KalmanFilter

`MotionProfile` and `KalmanFilter` can be used together in a mechanism with position feedback.

The motion profile generates the desired position. The Kalman filter estimates the measured position.

```java
MotionProfile profile = new MotionProfile(
        0.0,
        1200.0,
        800.0,
        1600.0
);

KalmanFilter filter = new KalmanFilter(
        0.1,
        4.0
);
```

Inside the OpMode loop:

```java
double elapsed = timer.seconds();

MotionProfile.State reference =
        profile.calculate(elapsed);

double measuredPosition =
        motor.getCurrentPosition();

double estimatedPosition =
        filter.update(measuredPosition);

double error =
        reference.position - estimatedPosition;
```

The resulting reference and estimate can be supplied to a feedback controller.

For a positional servo without an external position sensor, the Kalman filter cannot determine the actual shaft position from commanded positions alone.

# Publishing Updates

After adding new classes or modifying existing APIs, update the library's version before publishing.

For example, after publishing a new version, update your FTC project's dependency:

```gradle
implementation 'org.seramitae:seramitaelib:NEW_VERSION'
```

The exact publishing procedure depends on your Gradle publishing configuration and GitHub Actions workflow.

# AI Disclaimer

This README was created with the assistance of artificial intelligence (AI). AI was used to help organize the documentation, explain the library's features, and generate usage examples based on the intended behavior of its APIs.

While the documentation has been prepared to be as accurate as possible, some examples, descriptions, method signatures, or behaviors may contain errors or may not reflect the latest version of the library.

The SeramitaeLib source code should always be considered the definitive reference for the library's actual API and behavior.