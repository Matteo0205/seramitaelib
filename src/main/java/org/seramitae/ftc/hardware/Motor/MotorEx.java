package org.seramitae.ftc.hardware.Motor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MotorEx extends Motor {

    private final DcMotorEx motorEx;

    private double cachingTolerance = 0.0001;
    private double lastPower = Double.NaN;

    public MotorEx(HardwareMap hardwareMap, String name) {
        super(hardwareMap, name);

        motorEx = raw();
    }

    public MotorEx(
            HardwareMap hardwareMap,
            String name,
            double cpr,
            double rpm
    ) {
        super(
                hardwareMap,
                name,
                cpr,
                rpm
        );

        motorEx = raw();
    }

    @Override
    public MotorEx setPower(double power) {

        if (Double.isNaN(power)) {
            throw new IllegalArgumentException(
                    "Motor power must not be NaN"
            );
        }

        power = clamp(
                power,
                -getMaxPower(),
                getMaxPower()
        );

        if (
                Double.isNaN(lastPower)
                        || Math.abs(power - lastPower) >= cachingTolerance
        ) {
            motorEx.setPower(power);
            lastPower = power;
        }

        return this;
    }

    @Override
    public MotorEx set(double output) {
        super.set(output);
        return this;
    }

    @Override
    public MotorEx stopMotor() {
        setPower(0);
        return this;
    }

    @Override
    public MotorEx stop() {
        return stopMotor();
    }

    public MotorEx setVelocity(double ticksPerSecond) {

        if (Double.isNaN(ticksPerSecond)) {
            throw new IllegalArgumentException(
                    "Velocity must not be NaN"
            );
        }

        motorEx.setVelocity(ticksPerSecond);

        return this;
    }

    public MotorEx setVelocity(
            double angularRate,
            AngleUnit unit
    ) {

        if (Double.isNaN(angularRate)) {
            throw new IllegalArgumentException(
                    "Velocity must not be NaN"
            );
        }

        motorEx.setVelocity(
                angularRate,
                unit
        );

        return this;
    }

    @Override
    public double getVelocity() {
        return motorEx.getVelocity();
    }

    public double getVelocity(AngleUnit unit) {
        return motorEx.getVelocity(unit);
    }

    @Override
    public double getCorrectedVelocity() {
        return encoder.getCorrectedVelocity();
    }

    public MotorEx setCachingTolerance(double tolerance) {

        if (
                Double.isNaN(tolerance)
                        || tolerance < 0
        ) {
            throw new IllegalArgumentException(
                    "Caching tolerance must be >= 0"
            );
        }

        cachingTolerance = tolerance;

        return this;
    }

    public double getCachingTolerance() {
        return cachingTolerance;
    }

    public MotorEx clearCache() {
        lastPower = Double.NaN;
        return this;
    }

    public MotorEx forceSetPower(double power) {

        if (Double.isNaN(power)) {
            throw new IllegalArgumentException(
                    "Motor power must not be NaN"
            );
        }

        power = clamp(
                power,
                -getMaxPower(),
                getMaxPower()
        );

        motorEx.setPower(power);
        lastPower = power;

        return this;
    }

    public double getCurrent(CurrentUnit unit) {
        return motorEx.getCurrent(unit);
    }

    public double getCurrent() {
        return getCurrent(
                CurrentUnit.AMPS
        );
    }

    public MotorEx setCurrentAlert(
            double current,
            CurrentUnit unit
    ) {

        if (current < 0) {
            throw new IllegalArgumentException(
                    "Current alert cannot be negative"
            );
        }

        motorEx.setCurrentAlert(
                current,
                unit
        );

        return this;
    }

    public double getCurrentAlert(
            CurrentUnit unit
    ) {
        return motorEx.getCurrentAlert(unit);
    }

    public boolean isOverCurrent() {
        return motorEx.isOverCurrent();
    }

    public boolean isStalled(
            double minimumPower,
            double maximumVelocity
    ) {

        return Math.abs(getPower()) >= minimumPower
                && Math.abs(getVelocity()) <= maximumVelocity;
    }

    public boolean isStalled(
            double minimumPower,
            double maximumVelocity,
            double minimumCurrent
    ) {

        return Math.abs(getPower()) >= minimumPower
                && Math.abs(getVelocity()) <= maximumVelocity
                && getCurrent() >= minimumCurrent;
    }

    @Override
    public DcMotorEx raw() {
        return motorEx != null
                ? motorEx
                : super.raw();
    }

    private static double clamp(
            double value,
            double minimum,
            double maximum
    ) {
        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }
}