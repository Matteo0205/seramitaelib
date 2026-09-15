package org.seramitae.ftc.hardware.Motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MotorGroup {

    private final Motor[] motors;
    private final Motor leader;

    public MotorGroup(Motor... motors) {

        if (motors == null || motors.length == 0) {
            throw new IllegalArgumentException(
                    "MotorGroup requires at least one motor"
            );
        }

        for (Motor motor : motors) {
            if (motor == null) {
                throw new IllegalArgumentException(
                        "MotorGroup cannot contain null motors"
                );
            }
        }

        this.motors = motors.clone();
        this.leader = this.motors[0];
    }

    public MotorGroup set(double output) {
        for (Motor motor : motors) {
            motor.set(output);
        }

        return this;
    }

    public MotorGroup setPower(double power) {
        for (Motor motor : motors) {
            motor.setPower(power);
        }

        return this;
    }

    public MotorGroup stop() {
        for (Motor motor : motors) {
            motor.stop();
        }

        return this;
    }

    public MotorGroup brake() {
        for (Motor motor : motors) {
            motor.brake();
        }

        return this;
    }

    public MotorGroup coast() {
        for (Motor motor : motors) {
            motor.coast();
        }

        return this;
    }

    public MotorGroup setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior behavior
    ) {
        for (Motor motor : motors) {
            motor.setZeroPowerBehavior(behavior);
        }

        return this;
    }

    public MotorGroup setDirection(
            DcMotorSimple.Direction direction
    ) {
        for (Motor motor : motors) {
            motor.setDirection(direction);
        }

        return this;
    }

    public MotorGroup setInverted(boolean inverted) {
        for (Motor motor : motors) {
            motor.setInverted(inverted);
        }

        return this;
    }

    public MotorGroup setInverted(
            int index,
            boolean inverted
    ) {
        get(index).setInverted(inverted);
        return this;
    }

    public MotorGroup setRunMode(Motor.RunMode mode) {
        for (Motor motor : motors) {
            motor.setRunMode(mode);
        }

        return this;
    }

    public MotorGroup setMaxPower(double maxPower) {
        for (Motor motor : motors) {
            motor.setMaxPower(maxPower);
        }

        return this;
    }

    public MotorGroup setTargetPosition(int ticks) {
        for (Motor motor : motors) {
            motor.setTargetPosition(ticks);
        }

        return this;
    }

    public MotorGroup setTargetPosition(
            int index,
            int ticks
    ) {
        get(index).setTargetPosition(ticks);
        return this;
    }

    public MotorGroup setPositionCoefficient(
            double coefficient
    ) {
        for (Motor motor : motors) {
            motor.setPositionCoefficient(coefficient);
        }

        return this;
    }

    public MotorGroup setPositionTolerance(
            double tolerance
    ) {
        for (Motor motor : motors) {
            motor.setPositionTolerance(tolerance);
        }

        return this;
    }

    public MotorGroup setVeloCoefficients(
            double kP,
            double kI,
            double kD
    ) {
        for (Motor motor : motors) {
            motor.setVeloCoefficients(kP, kI, kD);
        }

        return this;
    }

    public MotorGroup setFeedforwardCoefficients(
            double kS,
            double kV
    ) {
        for (Motor motor : motors) {
            motor.setFeedforwardCoefficients(kS, kV);
        }

        return this;
    }

    public MotorGroup setFeedforwardCoefficients(
            double kS,
            double kV,
            double kA
    ) {
        for (Motor motor : motors) {
            motor.setFeedforwardCoefficients(kS, kV, kA);
        }

        return this;
    }

    public MotorGroup resetEncoder() {
        for (Motor motor : motors) {
            motor.resetEncoder();
        }

        return this;
    }

    public MotorGroup stopAndResetEncoder() {
        for (Motor motor : motors) {
            motor.stopAndResetEncoder();
        }

        return this;
    }

    public MotorGroup setDistancePerPulse(
            double distancePerPulse
    ) {
        for (Motor motor : motors) {
            motor.setDistancePerPulse(distancePerPulse);
        }

        return this;
    }

    public double getPower() {
        return leader.getPower();
    }

    public int getCurrentPosition() {
        return leader.getCurrentPosition();
    }

    public int getTargetPosition() {
        return leader.getTargetPosition();
    }

    public double getVelocity() {
        return leader.getVelocity();
    }

    public double getDistance() {
        return leader.getDistance();
    }

    public boolean atTargetPosition() {
        for (Motor motor : motors) {
            if (!motor.atTargetPosition()) {
                return false;
            }
        }

        return true;
    }

    public boolean isBusy() {
        for (Motor motor : motors) {
            if (motor.isBusy()) {
                return true;
            }
        }

        return false;
    }

    public Motor getLeader() {
        return leader;
    }

    public Motor get(int index) {

        if (index < 0 || index >= motors.length) {
            throw new IndexOutOfBoundsException(
                    "Motor index out of range: " + index
            );
        }

        return motors[index];
    }

    public int size() {
        return motors.length;
    }

    public List<Motor> getMotors() {
        return Collections.unmodifiableList(
                Arrays.asList(motors)
        );
    }
}