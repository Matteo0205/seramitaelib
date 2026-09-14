package org.seramitae.ftc.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motor {
    private final DcMotorEx motor;
    private double maxPower = 1.0;

    public Motor(HardwareMap hardwareMap, String name) {
        motor = hardwareMap.get(DcMotorEx.class, name);
    }

    public Motor setPower(double power) {
        motor.setPower(clamp(power, -maxPower, maxPower));
        return this;
    }

    public Motor stop() {
        return setPower(0.0);
    }

    public Motor reverse() {
        return setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public Motor forward() {
        return setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public Motor brake() {
        return setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public Motor coast() {
        return setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public Motor setDirection(DcMotorSimple.Direction direction) {
        motor.setDirection(direction);
        return this;
    }

    public Motor setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        motor.setZeroPowerBehavior(behavior);
        return this;
    }

    public Motor setMode(DcMotor.RunMode mode) {
        motor.setMode(mode);
        return this;
    }

    public Motor resetEncoder() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        return this;
    }

    public Motor setTargetPosition(int ticks) {
        motor.setTargetPosition(ticks);
        return this;
    }

    public Motor setVelocity(double ticksPerSecond) {
        motor.setVelocity(ticksPerSecond);
        return this;
    }

    public int getPosition() {
        return motor.getCurrentPosition();
    }

    public int getTargetPosition() {
        return motor.getTargetPosition();
    }

    public double getVelocity() {
        return motor.getVelocity();
    }

    public double getPower() {
        return motor.getPower();
    }

    public boolean isBusy() {
        return motor.isBusy();
    }

    public Motor setMaxPower(double maxPower) {
        this.maxPower = clamp(maxPower, 0.0, 1.0);
        return this;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public Motor setInverted(boolean inverted) {
        return setDirection(inverted ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }

    public boolean isInverted() {
        return motor.getDirection() == DcMotorSimple.Direction.REVERSE;
    }

    public DcMotorEx raw() {
        return motor;
    }

    private static double clamp(double value, double minimum, double maximum) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("Motor power must not be NaN");
        }
        return Math.max(minimum, Math.min(maximum, value));
    }
}
