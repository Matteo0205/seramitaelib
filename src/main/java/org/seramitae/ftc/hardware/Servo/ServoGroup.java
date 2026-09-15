package org.seramitae.ftc.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServoGroup {

    private final ServoEx[] servos;
    private final ServoEx leader;

    public ServoGroup(ServoEx... servos) {

        if (servos == null || servos.length == 0) {
            throw new IllegalArgumentException(
                    "ServoGroup requires at least one servo"
            );
        }

        for (ServoEx servo : servos) {
            if (servo == null) {
                throw new IllegalArgumentException(
                        "ServoGroup cannot contain null servos"
                );
            }
        }

        this.servos = servos.clone();
        this.leader = this.servos[0];
    }

    public ServoGroup setPosition(double position) {
        for (ServoEx servo : servos) {
            servo.setPosition(position);
        }

        return this;
    }

    public ServoGroup rotateBy(double amount) {
        for (ServoEx servo : servos) {
            servo.rotateBy(amount);
        }

        return this;
    }

    public ServoGroup turnToAngle(double angle) {
        for (ServoEx servo : servos) {
            servo.turnToAngle(angle);
        }

        return this;
    }

    public ServoGroup turnToAngle(
            double angle,
            AngleUnit unit
    ) {
        for (ServoEx servo : servos) {
            servo.turnToAngle(angle, unit);
        }

        return this;
    }

    public ServoGroup rotateByAngle(double angle) {
        for (ServoEx servo : servos) {
            servo.rotateByAngle(angle);
        }

        return this;
    }

    public ServoGroup rotateByAngle(
            double angle,
            AngleUnit unit
    ) {
        for (ServoEx servo : servos) {
            servo.rotateByAngle(angle, unit);
        }

        return this;
    }

    public ServoGroup setRange(
            double minAngle,
            double maxAngle
    ) {
        for (ServoEx servo : servos) {
            servo.setRange(
                    minAngle,
                    maxAngle
            );
        }

        return this;
    }

    public ServoGroup setRange(
            double minAngle,
            double maxAngle,
            AngleUnit unit
    ) {
        for (ServoEx servo : servos) {
            servo.setRange(
                    minAngle,
                    maxAngle,
                    unit
            );
        }

        return this;
    }

    public ServoGroup setInverted(boolean inverted) {
        for (ServoEx servo : servos) {
            servo.setInverted(inverted);
        }

        return this;
    }

    public ServoGroup setInverted(
            int index,
            boolean inverted
    ) {
        get(index).setInverted(inverted);
        return this;
    }

    public ServoGroup toggleInverted() {
        for (ServoEx servo : servos) {
            servo.toggleInverted();
        }

        return this;
    }

    public ServoGroup setCachingTolerance(
            double tolerance
    ) {
        for (ServoEx servo : servos) {
            servo.setCachingTolerance(tolerance);
        }

        return this;
    }

    public ServoGroup clearCache() {
        for (ServoEx servo : servos) {
            servo.clearCache();
        }

        return this;
    }

    public ServoGroup forceSetPosition(
            double position
    ) {
        for (ServoEx servo : servos) {
            servo.forceSetPosition(position);
        }

        return this;
    }

    public ServoGroup enablePwm() {
        for (ServoEx servo : servos) {
            servo.enablePwm();
        }

        return this;
    }

    public ServoGroup disablePwm() {
        for (ServoEx servo : servos) {
            servo.disablePwm();
        }

        return this;
    }

    public double getPosition() {
        return leader.getPosition();
    }

    public double getAngle() {
        return leader.getAngle();
    }

    public double getAngle(AngleUnit unit) {
        return leader.getAngle(unit);
    }

    public ServoEx getLeader() {
        return leader;
    }

    public ServoEx get(int index) {

        if (index < 0 || index >= servos.length) {
            throw new IndexOutOfBoundsException(
                    "Servo index out of range: " + index
            );
        }

        return servos[index];
    }

    public int size() {
        return servos.length;
    }

    public List<ServoEx> getServos() {
        return Collections.unmodifiableList(
                Arrays.asList(servos)
        );
    }
}