package org.seramitae.ftc.hardware.Servo;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class CRServoEx {

    private final CRServo servo;

    private double lastPower = Double.NaN;
    private double cachingTolerance = 0.0001;

    private boolean inverted = false;

    public CRServoEx(HardwareMap hardwareMap, String id) {
        servo = hardwareMap.get(CRServo.class, id);
    }

    public void setPower(double power) {
        power = clamp(power, -1.0, 1.0);

        double output = inverted
                ? -power
                : power;

        if (Double.isNaN(lastPower)
                || Math.abs(output - lastPower) >= cachingTolerance) {

            servo.setPower(output);
            lastPower = output;
        }
    }

    public double getPower() {
        double power = servo.getPower();

        return inverted
                ? -power
                : power;
    }

    public void stop() {
        setPower(0);
    }

    public void forward() {
        setPower(1);
    }

    public void reverse() {
        setPower(-1);
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean getInverted() {
        return inverted;
    }

    public void toggleInverted() {
        inverted = !inverted;
    }

    public void setDirection(DcMotorSimple.Direction direction) {
        servo.setDirection(direction);
        clearCache();
    }

    public DcMotorSimple.Direction getDirection() {
        return servo.getDirection();
    }

    public void setCachingTolerance(double tolerance) {
        if (tolerance < 0) {
            throw new IllegalArgumentException(
                    "Caching tolerance cannot be negative"
            );
        }

        cachingTolerance = tolerance;
    }

    public double getCachingTolerance() {
        return cachingTolerance;
    }

    public void clearCache() {
        lastPower = Double.NaN;
    }

    public void forceSetPower(double power) {
        power = clamp(power, -1.0, 1.0);

        double output = inverted
                ? -power
                : power;

        servo.setPower(output);
        lastPower = output;
    }

    public CRServo getServo() {
        return servo;
    }

    public String getDeviceName() {
        return servo.getDeviceName();
    }

    public String getDeviceType() {
        return "CRServoEx";
    }

    public void resetDeviceConfigurationForOpMode() {
        servo.resetDeviceConfigurationForOpMode();
        clearCache();
    }

    public void close() {
        servo.close();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }
}