package org.seramitae.ftc.hardware.Servo;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class ServoEx {

    private final Servo servo;
    private final ServoImplEx servoImplEx;

    private double minAngle = 0;
    private double maxAngle = 180;

    private boolean inverted = false;

    private double lastPosition = Double.NaN;
    private double cachingTolerance = 0.0001;

    public ServoEx(HardwareMap hardwareMap, String id) {
        servo = hardwareMap.get(Servo.class, id);

        if (servo instanceof ServoImplEx) {
            servoImplEx = (ServoImplEx) servo;
        } else {
            servoImplEx = null;
        }
    }

    public ServoEx(
            HardwareMap hardwareMap,
            String id,
            double minAngle,
            double maxAngle
    ) {
        this(hardwareMap, id);

        setRange(minAngle, maxAngle);
    }

    public ServoEx(
            HardwareMap hardwareMap,
            String id,
            double range,
            AngleUnit angleUnit
    ) {
        this(hardwareMap, id);

        this.minAngle = 0;
        this.maxAngle = toDegrees(range, angleUnit);

        validateRange();
    }

    public ServoEx(
            HardwareMap hardwareMap,
            String id,
            double minAngle,
            double maxAngle,
            AngleUnit angleUnit
    ) {
        this(hardwareMap, id);

        setRange(minAngle, maxAngle, angleUnit);
    }

    public void setPosition(double position) {
        position = clamp(position, 0, 1);

        double output = inverted
                ? 1.0 - position
                : position;

        if (Double.isNaN(lastPosition)
                || Math.abs(output - lastPosition) >= cachingTolerance) {

            servo.setPosition(output);
            lastPosition = output;
        }
    }

    public double getPosition() {
        double position = servo.getPosition();

        return inverted
                ? 1.0 - position
                : position;
    }

    public void rotateBy(double amount) {
        setPosition(getPosition() + amount);
    }

    public void turnToAngle(double angle) {
        turnToAngle(angle, AngleUnit.DEGREES);
    }

    public void turnToAngle(double angle, AngleUnit unit) {
        double degrees = toDegrees(angle, unit);

        degrees = clamp(
                degrees,
                minAngle,
                maxAngle
        );

        double position =
                (degrees - minAngle)
                        / (maxAngle - minAngle);

        setPosition(position);
    }

    public void rotateByAngle(double angle) {
        rotateByAngle(angle, AngleUnit.DEGREES);
    }

    public void rotateByAngle(double angle, AngleUnit unit) {
        double currentAngle = getAngle(AngleUnit.DEGREES);
        double change = toDegrees(angle, unit);

        turnToAngle(
                currentAngle + change,
                AngleUnit.DEGREES
        );
    }

    public double getAngle() {
        return getAngle(AngleUnit.DEGREES);
    }

    public double getAngle(AngleUnit unit) {
        double position = getPosition();

        double degrees =
                minAngle
                        + position * (maxAngle - minAngle);

        return fromDegrees(degrees, unit);
    }

    public void setRange(
            double minAngle,
            double maxAngle
    ) {
        setRange(
                minAngle,
                maxAngle,
                AngleUnit.DEGREES
        );
    }

    public void setRange(
            double minAngle,
            double maxAngle,
            AngleUnit unit
    ) {
        this.minAngle =
                toDegrees(minAngle, unit);

        this.maxAngle =
                toDegrees(maxAngle, unit);

        validateRange();
    }

    public double getAngleRange() {
        return getAngleRange(
                AngleUnit.DEGREES
        );
    }

    public double getAngleRange(AngleUnit unit) {
        double range =
                maxAngle - minAngle;

        return fromDegrees(range, unit);
    }

    public double getMinAngle() {
        return minAngle;
    }

    public double getMinAngle(AngleUnit unit) {
        return fromDegrees(minAngle, unit);
    }

    public double getMaxAngle() {
        return maxAngle;
    }

    public double getMaxAngle(AngleUnit unit) {
        return fromDegrees(maxAngle, unit);
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
        lastPosition = Double.NaN;
    }

    public void forceSetPosition(double position) {
        position = clamp(position, 0, 1);

        double output = inverted
                ? 1.0 - position
                : position;

        servo.setPosition(output);
        lastPosition = output;
    }

    public void setPwmRange(
            double lowerMicroseconds,
            double upperMicroseconds
    ) {
        if (servoImplEx == null) {
            throw new UnsupportedOperationException(
                    "This servo does not support custom PWM ranges"
            );
        }

        servoImplEx.setPwmRange(
                new PwmControl.PwmRange(
                        lowerMicroseconds,
                        upperMicroseconds
                )
        );
    }

    public void enablePwm() {
        if (servoImplEx != null) {
            servoImplEx.setPwmEnable();
        }
    }

    public void disablePwm() {
        if (servoImplEx != null) {
            servoImplEx.setPwmDisable();
        }
    }

    public boolean isPwmEnabled() {
        if (servoImplEx == null) {
            return true;
        }

        return servoImplEx.isPwmEnabled();
    }

    public Servo getServo() {
        return servo;
    }

    public ServoImplEx getServoImplEx() {
        return servoImplEx;
    }

    public String getDeviceType() {
        return "ServoEx";
    }

    public String getDeviceName() {
        return servo.getDeviceName();
    }

    public void resetDeviceConfigurationForOpMode() {
        servo.resetDeviceConfigurationForOpMode();
        clearCache();
    }

    public void close() {
        servo.close();
    }

    private void validateRange() {
        if (maxAngle <= minAngle) {
            throw new IllegalArgumentException(
                    "maxAngle must be greater than minAngle"
            );
        }
    }

    private double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }

    private double toDegrees(
            double angle,
            AngleUnit unit
    ) {
        if (unit == AngleUnit.RADIANS) {
            return Math.toDegrees(angle);
        }

        return angle;
    }

    private double fromDegrees(
            double degrees,
            AngleUnit unit
    ) {
        if (unit == AngleUnit.RADIANS) {
            return Math.toRadians(degrees);
        }

        return degrees;
    }
}