package org.seramitae.ftc.hardware.Lights;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RGBLight {

    public enum Color {
        OFF(0.000),
        RED(0.279),
        ORANGE(0.333),
        YELLOW(0.388),
        SAGE(0.444),
        GREEN(0.500),
        AZURE(0.555),
        BLUE(0.611),
        INDIGO(0.666),
        VIOLET(0.722),
        WHITE(1.000);

        private final double position;

        Color(double position) {
            this.position = position;
        }

        public double getPosition() {
            return position;
        }
    }

    private final Servo servo;

    private double lastPosition = Double.NaN;
    private double cachingTolerance = 0.0001;

    public RGBLight(HardwareMap hardwareMap, String name) {
        servo = hardwareMap.get(Servo.class, name);
    }

    public RGBLight(Servo servo) {
        if (servo == null) {
            throw new IllegalArgumentException("Servo cannot be null");
        }

        this.servo = servo;
    }

    public RGBLight setPosition(double position) {
        position = clamp(position, 0.0, 1.0);

        if (
                Double.isNaN(lastPosition)
                        || Math.abs(position - lastPosition) >= cachingTolerance
        ) {
            servo.setPosition(position);
            lastPosition = position;
        }

        return this;
    }

    public RGBLight setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }

        return setPosition(color.getPosition());
    }

    public RGBLight setColorFromRange(double percent) {
        percent = clamp(percent, 0.0, 100.0);

        return setPosition(percent / 100.0);
    }

    public double getPosition() {
        return servo.getPosition();
    }

    public RGBLight red() {
        return setColor(Color.RED);
    }

    public RGBLight orange() {
        return setColor(Color.ORANGE);
    }

    public RGBLight yellow() {
        return setColor(Color.YELLOW);
    }

    public RGBLight sage() {
        return setColor(Color.SAGE);
    }

    public RGBLight green() {
        return setColor(Color.GREEN);
    }

    public RGBLight azure() {
        return setColor(Color.AZURE);
    }

    public RGBLight blue() {
        return setColor(Color.BLUE);
    }

    public RGBLight indigo() {
        return setColor(Color.INDIGO);
    }

    public RGBLight violet() {
        return setColor(Color.VIOLET);
    }

    public RGBLight white() {
        return setColor(Color.WHITE);
    }

    public RGBLight off() {
        return setColor(Color.OFF);
    }

    public RGBLight setCachingTolerance(double tolerance) {
        if (Double.isNaN(tolerance) || tolerance < 0) {
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

    public RGBLight clearCache() {
        lastPosition = Double.NaN;
        return this;
    }

    public RGBLight forceSetPosition(double position) {
        position = clamp(position, 0.0, 1.0);

        servo.setPosition(position);
        lastPosition = position;

        return this;
    }

    public Servo raw() {
        return servo;
    }

    private static double clamp(
            double value,
            double minimum,
            double maximum
    ) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(
                    "Value must not be NaN"
            );
        }

        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }
}
