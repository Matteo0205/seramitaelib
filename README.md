# SeramitaeLib

A lightweight and easy-to-use hardware and control library for **FIRST Tech Challenge (FTC)** robots.

SeramitaeLib provides wrappers around common FTC SDK hardware components, hardware groups, controllers, and feedforward utilities to make robot code cleaner, easier to read, and faster to write.

## Features

SeramitaeLib currently includes:

### Hardware

* `Motor`
* `MotorEx`
* `MotorGroup`
* `ServoEx`
* `ServoGroup`
* `CRServoEx`
* `CRServoGroup`
* `RGBLight`

### Controllers

* `Controller`
* `PController`
* `PDController`
* `PIDController`
* `PIDFController`
* `SquIDController`
* `SquIDFController`

### Feedforward

* `SimpleMotorFeedforward`
* `ArmFeedforward`
* `ElevatorFeedforward`

### Other Features

* Motor encoder utilities
* Software encoder reset
* Position control
* Velocity control
* Motor current monitoring
* Motor stall detection
* Power caching
* Servo caching
* Hardware grouping
* Individual group-member inversion
* Position and velocity tolerances
* Integral limiting
* Minimum controller output
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
    implementation 'org.seramitae:seramitaelib:0.2.0'
}
```

Replace `0.2.0` with the version you want to use.

Your GitHub credentials can be stored in `gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

> **Important:** Never commit your GitHub token to a public repository.

---

# Motor

Import:

```java
import org.seramitae.ftc.hardware.Motor.Motor;
```

`Motor` is a wrapper around the FTC motor API that provides a simpler interface for controlling motors, encoders, position, and velocity.

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

## Power Control

```java
motor.setPower(1);
```

Stop:

```java
motor.stop();
```

You can also use:

```java
motor.set(0.5);
```

The behavior of `set()` depends on the selected `RunMode`.

## Maximum Power

```java
motor.setMaxPower(0.7);
```

Calling:

```java
motor.setPower(1);
```

will now limit the output to `0.7`.

Read the configured limit:

```java
double maxPower = motor.getMaxPower();
```

## Motor Direction

```java
motor.reverse();

motor.forward();

motor.setInverted(true);
```

Check inversion:

```java
boolean inverted = motor.isInverted();
```

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

SeramitaeLib provides three high-level motor modes:

```java
Motor.RunMode.RawPower
Motor.RunMode.VelocityControl
Motor.RunMode.PositionControl
```

Example:

```java
motor.setRunMode(
        Motor.RunMode.RawPower
);
```

## Raw Power

```java
motor.setRunMode(
        Motor.RunMode.RawPower
);

motor.set(0.8);
```

In `RawPower`, `set()` directly controls motor power.

---

# Motor Position Control

```java
motor.setRunMode(
        Motor.RunMode.PositionControl
);

motor.setPositionCoefficient(0.005);
motor.setPositionTolerance(15);

motor.setTargetPosition(1200);
```

Update the controller:

```java
motor.set(0.8);
```

The value passed to `set()` represents the maximum output allowed for the position controller.

Check the target:

```java
if (motor.atTargetPosition()) {
    motor.stop();
}
```

---

# Motor Velocity Control

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

represents 50% of the calculated maximum motor velocity.

---

# Encoder

Every `Motor` contains an encoder utility:

```java
motor.encoder
```

Position:

```java
int position =
        motor.getCurrentPosition();
```

or:

```java
int position =
        motor.encoder.getPosition();
```

Velocity:

```java
double velocity =
        motor.getVelocity();
```

Corrected velocity:

```java
double velocity =
        motor.getCorrectedVelocity();
```

## Software Encoder Reset

```java
motor.resetEncoder();
```

This resets the logical encoder position using a software offset without changing the FTC SDK motor run mode.

For a full hardware reset:

```java
motor.stopAndResetEncoder();
```

## Revolutions

When CPR is configured:

```java
double revolutions =
        motor.encoder.getRevolutions();
```

## Distance Per Pulse

```java
motor.setDistancePerPulse(0.01);
```

Read distance:

```java
double distance =
        motor.getDistance();
```

Set a target using distance:

```java
motor.setTargetDistance(50);
```

---

# MotorEx

Import:

```java
import org.seramitae.ftc.hardware.Motor.MotorEx;
```

`MotorEx` extends `Motor` and adds functionality designed around FTC's `DcMotorEx`.

Features include:

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

## Direct Velocity

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

motor.setVelocity(
        100,
        AngleUnit.RADIANS
);
```

Read angular velocity:

```java
double velocity =
        motor.getVelocity(
                AngleUnit.RADIANS
        );
```

## Power Caching

```java
motor.setCachingTolerance(0.01);
```

Repeated commands inside the configured tolerance do not require unnecessary hardware writes.

```java
motor.setPower(0.7);
motor.setPower(0.7);
motor.setPower(0.7);
```

Clear the cache:

```java
motor.clearCache();
```

Force a write:

```java
motor.forceSetPower(0.7);
```

## Current Monitoring

```java
double amps =
        motor.getCurrent();
```

Specify another unit:

```java
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

double milliamps =
        motor.getCurrent(
                CurrentUnit.MILLIAMPS
        );
```

## Current Alerts

```java
motor.setCurrentAlert(
        5,
        CurrentUnit.AMPS
);
```

Then:

```java
if (motor.isOverCurrent()) {
    motor.stop();
}
```

## Stall Detection

Using power and velocity:

```java
if (motor.isStalled(
        0.8,
        50
)) {
    motor.stop();
}
```

Using power, velocity, and current:

```java
if (motor.isStalled(
        0.8,
        50,
        4.5
)) {
    motor.stop();
}
```

---

# MotorGroup

Import:

```java
import org.seramitae.ftc.hardware.Motor.MotorGroup;
```

`MotorGroup` allows multiple motors to be controlled as a single logical motor group.

Create two motors:

```java
MotorEx leftFront =
        new MotorEx(
                hardwareMap,
                "leftFront"
        );

MotorEx leftBack =
        new MotorEx(
                hardwareMap,
                "leftBack"
        );
```

Create the group:

```java
MotorGroup left = new MotorGroup(
        leftFront,
        leftBack
);
```

Control both:

```java
left.setPower(1);
```

Stop both:

```java
left.stop();
```

Configure both:

```java
left
        .brake()
        .setMaxPower(0.8)
        .setPower(1);
```

## Individual Motor Inversion

A specific motor can be inverted:

```java
left.setInverted(
        1,
        true
);
```

For example, a dual-motor mechanism can use:

```java
MotorGroup shooter =
        new MotorGroup(
                shooterLeft,
                shooterRight
        );

shooter.setInverted(0, false);
shooter.setInverted(1, true);

shooter.setPower(1);
```

## Group Position Control

```java
liftGroup.setRunMode(
        Motor.RunMode.PositionControl
);

liftGroup.setPositionCoefficient(0.005);
liftGroup.setPositionTolerance(15);
liftGroup.setTargetPosition(1500);
```

Then:

```java
liftGroup.set(1);
```

Check all motors:

```java
if (liftGroup.atTargetPosition()) {
    liftGroup.stop();
}
```

## Access Individual Motors

Leader:

```java
Motor leader =
        group.getLeader();
```

Specific motor:

```java
Motor second =
        group.get(1);
```

Number of motors:

```java
int count =
        group.size();
```

---

# ServoEx

Import:

```java
import org.seramitae.ftc.hardware.Servo.ServoEx;
```

Create:

```java
ServoEx claw = new ServoEx(
        hardwareMap,
        "claw"
);
```

With angular range:

```java
ServoEx claw = new ServoEx(
        hardwareMap,
        "claw",
        0,
        180
);
```

## Position

```java
claw.setPosition(0.5);
```

Read:

```java
double position =
        claw.getPosition();
```

Relative movement:

```java
claw.rotateBy(0.1);
```

## Angle Control

```java
claw.turnToAngle(90);
```

Relative:

```java
claw.rotateByAngle(20);
```

Read:

```java
double angle =
        claw.getAngle();
```

Radians:

```java
claw.turnToAngle(
        Math.PI / 2,
        AngleUnit.RADIANS
);
```

## Range

```java
claw.setRange(
        20,
        160
);
```

## Inversion

```java
claw.setInverted(true);

claw.toggleInverted();
```

## Caching

```java
claw.setCachingTolerance(0.01);

claw.clearCache();

claw.forceSetPosition(0.5);
```

## PWM

```java
claw.setPwmRange(
        500,
        2500
);

claw.disablePwm();

claw.enablePwm();
```

---

# ServoGroup

Import:

```java
import org.seramitae.ftc.hardware.Servo.ServoGroup;
```

A `ServoGroup` allows multiple `ServoEx` objects to behave like a single servo mechanism.

```java
ServoEx left =
        new ServoEx(
                hardwareMap,
                "left"
        );

ServoEx right =
        new ServoEx(
                hardwareMap,
                "right"
        );

ServoGroup arm =
        new ServoGroup(
                left,
                right
        );
```

Move both:

```java
arm.setPosition(0.5);
```

Or by angle:

```java
arm.setRange(0, 180);

arm.turnToAngle(90);
```

## Individual Inversion

Very useful for mirrored servos:

```java
arm.setInverted(0, false);
arm.setInverted(1, true);
```

You can then treat the entire mechanism as one device:

```java
arm.setPosition(0.7);
```

## Relative Movement

```java
arm.rotateBy(0.1);

arm.rotateByAngle(20);
```

## Group Caching

```java
arm.setCachingTolerance(0.01);

arm.clearCache();
```

## Group PWM

```java
arm.disablePwm();

arm.enablePwm();
```

## Access Group Members

```java
ServoEx leader =
        arm.getLeader();

ServoEx second =
        arm.get(1);

int count =
        arm.size();
```

---

# CRServoEx

Import:

```java
import org.seramitae.ftc.hardware.Servo.CRServoEx;
```

Create:

```java
CRServoEx intake =
        new CRServoEx(
                hardwareMap,
                "intake"
        );
```

Power:

```java
intake.setPower(1);
```

Convenience methods:

```java
intake.forward();

intake.reverse();

intake.stop();
```

## Inversion

```java
intake.setInverted(true);

intake.toggleInverted();
```

## Caching

```java
intake.setCachingTolerance(0.01);

intake.clearCache();

intake.forceSetPower(1);
```

---

# CRServoGroup

Import:

```java
import org.seramitae.ftc.hardware.Servo.CRServoGroup;
```

Create:

```java
CRServoEx left =
        new CRServoEx(
                hardwareMap,
                "left"
        );

CRServoEx right =
        new CRServoEx(
                hardwareMap,
                "right"
        );

CRServoGroup intake =
        new CRServoGroup(
                left,
                right
        );
```

Control the entire group:

```java
intake.setPower(1);
```

Convenience methods:

```java
intake.forward();

intake.reverse();

intake.stop();
```

## Individual Inversion

```java
intake.setInverted(0, false);
intake.setInverted(1, true);
```

Then:

```java
intake.setPower(1);
```

can drive mirrored CR servos in the correct physical direction.

## Caching

```java
intake.setCachingTolerance(0.01);

intake.clearCache();

intake.forceSetPower(1);
```

## Access Members

```java
CRServoEx leader =
        intake.getLeader();

CRServoEx second =
        intake.get(1);

int count =
        intake.size();
```

---

# RGBLight

Import:

```java
import org.seramitae.ftc.hardware.Lights.RGBLight;
```

Create:

```java
RGBLight light =
        new RGBLight(
                hardwareMap,
                "rgb"
        );
```

## Built-In Colors

Available colors:

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

Convenience methods:

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

Enum:

```java
light.setColor(
        RGBLight.Color.RED
);
```

Example status indicator:

```java
if (motor.isOverCurrent()) {
    light.red();
} else {
    light.green();
}
```

## Range

```java
light.setPosition(0.5);

light.setColorFromRange(50);
```

## Caching

```java
light.setCachingTolerance(0.001);

light.clearCache();

light.forceSetPosition(0.5);
```

---

# Controllers

SeramitaeLib includes reusable feedback controllers under:

```java
org.seramitae.ftc.controller
```

The controller system includes:

```text
Controller
├── PController
├── PDController
├── PIDController
├── PIDFController
├── SquIDController
└── SquIDFController
```

---

# Controller Base

`Controller` is the common base for SeramitaeLib feedback controllers.

Common functionality includes:

* Setpoint management
* Position error
* Velocity error
* Position tolerance
* Velocity tolerance
* Minimum output
* Timing
* Resetting
* `atSetPoint()`

Example:

```java
controller.setSetPoint(1000);

double output =
        controller.calculate(
                currentPosition
        );
```

Check target:

```java
if (controller.atSetPoint()) {
    motor.stop();
}
```

## Tolerance

Position tolerance:

```java
controller.setTolerance(10);
```

Position and velocity tolerance:

```java
controller.setTolerance(
        10,
        5
);
```

## Minimum Output

```java
controller.setMinimumOutput(0.05);
```

This can help mechanisms overcome static friction when the calculated controller output becomes very small.

## Errors

```java
double positionError =
        controller.getPositionError();

double velocityError =
        controller.getVelocityError();
```

## Reset

```java
controller.reset();
```

---

# PController

Import:

```java
import org.seramitae.ftc.controller.PController;
```

Create:

```java
PController controller =
        new PController(
                0.005
        );
```

Set target:

```java
controller.setSetPoint(1200);
```

Use it:

```java
double power =
        controller.calculate(
                motor.getCurrentPosition()
        );

motor.setPower(power);
```

---

# PDController

Import:

```java
import org.seramitae.ftc.controller.PDController;
```

Create:

```java
PDController controller =
        new PDController(
                0.005,
                0.0002
        );
```

Use:

```java
controller.setSetPoint(1200);

motor.setPower(
        controller.calculate(
                motor.getCurrentPosition()
        )
);
```

---

# PIDController

Import:

```java
import org.seramitae.ftc.controller.PIDController;
```

Create:

```java
PIDController pid =
        new PIDController(
                0.01,
                0.0001,
                0.002
        );
```

Set target:

```java
pid.setSetPoint(1500);
```

In the OpMode loop:

```java
double power =
        pid.calculate(
                motor.getCurrentPosition()
        );

motor.setPower(power);
```

Check target:

```java
if (pid.atSetPoint()) {
    motor.stop();
}
```

Update coefficients:

```java
pid.setPID(
        0.012,
        0.0001,
        0.001
);
```

---

# PIDFController

Import:

```java
import org.seramitae.ftc.controller.PIDFController;
```

Create:

```java
PIDFController controller =
        new PIDFController(
                0.01,
                0.0001,
                0.001,
                0.05
        );
```

Set target:

```java
controller.setSetPoint(1500);
```

Calculate:

```java
double output =
        controller.calculate(
                motor.getCurrentPosition()
        );
```

Update individual coefficients:

```java
controller.setP(0.02);

controller.setI(0.0002);

controller.setD(0.001);

controller.setF(0.05);
```

Or all at once:

```java
controller.setPIDF(
        0.02,
        0.0002,
        0.001,
        0.05
);
```

## Integral Bounds

Integral windup can be limited using:

```java
controller.setIntegralBounds(
        -0.3,
        0.3
);
```

Read accumulated integral:

```java
double integral =
        controller.getIntegral();
```

---

# SquIDController

Import:

```java
import org.seramitae.ftc.controller.SquIDController;
```

Create:

```java
SquIDController controller =
        new SquIDController(
                0.05
        );
```

Set target:

```java
controller.setSetPoint(1000);
```

Calculate output:

```java
double output =
        controller.calculate(
                motor.getCurrentPosition()
        );

motor.setPower(output);
```

SquID uses a nonlinear response based on the square root of the absolute position error.

This can provide stronger response when far from the target while becoming smoother as the mechanism approaches the setpoint.

---

# SquIDFController

Import:

```java
import org.seramitae.ftc.controller.SquIDFController;
```

Create:

```java
SquIDFController controller =
        new SquIDFController(
                0.05,
                0.03
        );
```

Set target:

```java
controller.setSetPoint(1000);
```

Use:

```java
motor.setPower(
        controller.calculate(
                motor.getCurrentPosition()
        )
);
```

Update coefficients:

```java
controller.setSquIDF(
        0.06,
        0.025
);
```

---

# Feedforward

Feedforward helpers are available under:

```java
org.seramitae.ftc.controller.feedforward
```

Available helpers:

```text
SimpleMotorFeedforward
ArmFeedforward
ElevatorFeedforward
```

---

# SimpleMotorFeedforward

Import:

```java
import org.seramitae.ftc.controller.feedforward.SimpleMotorFeedforward;
```

Useful for mechanisms such as:

* Flywheels
* Shooters
* Drivetrains
* Other rotating mechanisms

Create:

```java
SimpleMotorFeedforward ff =
        new SimpleMotorFeedforward(
                0.07,
                0.000365
        );
```

With acceleration compensation:

```java
SimpleMotorFeedforward ff =
        new SimpleMotorFeedforward(
                0.07,
                0.000365,
                0.00005
        );
```

Calculate:

```java
double output =
        ff.calculate(
                targetVelocity
        );
```

With acceleration:

```java
double output =
        ff.calculate(
                targetVelocity,
                targetAcceleration
        );
```

---

# ArmFeedforward

Import:

```java
import org.seramitae.ftc.controller.feedforward.ArmFeedforward;
```

Designed for rotating arm mechanisms where gravity changes depending on arm angle.

Create:

```java
ArmFeedforward ff =
        new ArmFeedforward(
                0.05,
                0.15,
                0.01
        );
```

Calculate:

```java
double output =
        ff.calculate(
                Math.toRadians(armAngle),
                targetVelocity
        );
```

With acceleration:

```java
double output =
        ff.calculate(
                Math.toRadians(armAngle),
                targetVelocity,
                targetAcceleration
        );
```

---

# ElevatorFeedforward

Import:

```java
import org.seramitae.ftc.controller.feedforward.ElevatorFeedforward;
```

Designed for vertically moving mechanisms such as:

* Slides
* Elevators
* Vertical lifts

Create:

```java
ElevatorFeedforward ff =
        new ElevatorFeedforward(
                0.05,
                0.10,
                0.001
        );
```

Calculate:

```java
double output =
        ff.calculate(
                targetVelocity
        );
```

With acceleration:

```java
double output =
        ff.calculate(
                targetVelocity,
                targetAcceleration
        );
```

---

# PID + Feedforward

Feedback and feedforward can be combined.

This is especially useful for mechanisms such as flywheel shooters.

```java
PIDController pid =
        new PIDController(
                0.004,
                0.0001,
                0.00005
        );

SimpleMotorFeedforward ff =
        new SimpleMotorFeedforward(
                0.07,
                0.000365
        );

pid.setSetPoint(1500);
```

Then inside the OpMode loop:

```java
double velocity =
        shooter.getVelocity();

double feedback =
        pid.calculate(velocity);

double feedforward =
        ff.calculate(1500);

double output =
        feedback + feedforward;

shooter.setPower(output);
```

The control flow is approximately:

```text
Target
  │
  ├──────────────► Feedforward ──────┐
  │                                  │
  ▼                                  ▼
Error ───────────► PID ────────────► (+)
  ▲                                  │
  │                                  ▼
Sensor ◄──────── Mechanism ◄────── Output
```

---

# Complete Position Control Example

```java
@TeleOp(name = "PID Lift Example")
public class PIDLiftExample extends OpMode {

    private MotorEx lift;
    private PIDController controller;

    @Override
    public void init() {

        lift = new MotorEx(
                hardwareMap,
                "lift"
        );

        controller =
                new PIDController(
                        0.005,
                        0.0001,
                        0.0002
                );

        controller.setTolerance(
                15,
                10
        );

        controller.setSetPoint(1500);

        lift.brake();
    }

    @Override
    public void loop() {

        double position =
                lift.getCurrentPosition();

        double output =
                controller.calculate(
                        position
                );

        lift.setPower(output);

        telemetry.addData(
                "Position",
                position
        );

        telemetry.addData(
                "Target",
                controller.getSetPoint()
        );

        telemetry.addData(
                "Error",
                controller.getPositionError()
        );

        telemetry.addData(
                "At Target",
                controller.atSetPoint()
        );

        telemetry.update();
    }
}
```

---

# Complete Shooter Example

```java
@TeleOp(name = "Shooter Example")
public class ShooterExample extends OpMode {

    private MotorEx shooter;

    private PIDController pid;

    private SimpleMotorFeedforward ff;

    private static final double TARGET =
            1500;

    @Override
    public void init() {

        shooter =
                new MotorEx(
                        hardwareMap,
                        "shooter"
                );

        pid =
                new PIDController(
                        0.004,
                        0.0001,
                        0.00005
                );

        ff =
                new SimpleMotorFeedforward(
                        0.07,
                        0.000365
                );

        pid.setSetPoint(TARGET);

        pid.setTolerance(25);
    }

    @Override
    public void loop() {

        double velocity =
                shooter.getVelocity();

        double feedback =
                pid.calculate(
                        velocity
                );

        double feedforward =
                ff.calculate(
                        TARGET
                );

        double output =
                feedback
                + feedforward;

        shooter.setPower(output);

        telemetry.addData(
                "Target",
                TARGET
        );

        telemetry.addData(
                "Velocity",
                velocity
        );

        telemetry.addData(
                "Error",
                pid.getPositionError()
        );

        telemetry.addData(
                "Ready",
                pid.atSetPoint()
        );

        telemetry.update();
    }
}
```

---

# Fluent API

Many SeramitaeLib classes return themselves from configuration methods, allowing method chaining.

Motor:

```java
MotorEx motor =
        new MotorEx(
                hardwareMap,
                "motor"
        )
        .setMaxPower(0.8)
        .brake()
        .setCachingTolerance(0.01);
```

RGB:

```java
RGBLight status =
        new RGBLight(
                hardwareMap,
                "status"
        )
        .setCachingTolerance(0.001)
        .blue();
```

Groups:

```java
MotorGroup drivetrain =
        new MotorGroup(
                leftFront,
                leftBack
        )
        .brake()
        .setMaxPower(0.8);
```

---

# Raw Hardware Access

SeramitaeLib does not prevent direct access to the FTC SDK.

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

This allows unsupported or advanced FTC SDK functionality to still be used when necessary.

---

# Project Structure

The current SeramitaeLib structure is:

```text
org.seramitae.ftc
│
├── controller
│   │
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
└── hardware
    │
    ├── Motor
    │   ├── Motor.java
    │   ├── MotorEx.java
    │   └── MotorGroup.java
    │
    ├── Servo
    │   ├── ServoEx.java
    │   ├── ServoGroup.java
    │   ├── CRServoEx.java
    │   └── CRServoGroup.java
    │
    └── Lights
        └── RGBLight.java
```

---

# Design Goals

SeramitaeLib aims to provide:

* Cleaner FTC robot code
* Less repetitive hardware code
* Easy-to-use hardware wrappers
* Reusable feedback controllers
* Feedforward utilities
* Better hardware write efficiency
* Useful motor safety utilities
* Simple encoder utilities
* Multi-device hardware groups
* Readable APIs
* Fluent configuration
* Compatibility with the FTC SDK
* Direct access to the underlying FTC hardware when needed

The library is designed so developers can use as much or as little of it as they want without losing access to the standard FTC SDK.

---

# Contributing

Contributions, bug reports, and feature suggestions are welcome.

When reporting a problem, consider including:

* SeramitaeLib version
* FTC SDK version
* Relevant code
* Expected behavior
* Actual behavior
* Error logs or stack traces



---




## AI Disclaimer

This README was created with the assistance of artificial intelligence (AI). AI was used to help organize the documentation, explain the library's features, and generate usage examples based on the SeramitaeLib source code and the intended behavior of its APIs.

While the documentation has been prepared to be as accurate as possible, some examples, descriptions, method signatures, or behaviors may contain errors or may not reflect the latest version of the library. The SeramitaeLib source code should always be considered the definitive reference for the library's actual API and behavior.
