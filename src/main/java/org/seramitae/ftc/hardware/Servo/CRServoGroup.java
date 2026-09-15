package org.seramitae.ftc.hardware.Servo;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CRServoGroup {

    private final CRServoEx[] servos;
    private final CRServoEx leader;

    public CRServoGroup(CRServoEx... servos) {

        if (servos == null || servos.length == 0) {
            throw new IllegalArgumentException(
                    "CRServoGroup requires at least one servo"
            );
        }

        for (CRServoEx servo : servos) {
            if (servo == null) {
                throw new IllegalArgumentException(
                        "CRServoGroup cannot contain null servos"
                );
            }
        }

        this.servos = servos.clone();
        this.leader = this.servos[0];
    }

    public CRServoGroup setPower(double power) {
        for (CRServoEx servo : servos) {
            servo.setPower(power);
        }

        return this;
    }

    public CRServoGroup stop() {
        for (CRServoEx servo : servos) {
            servo.stop();
        }

        return this;
    }

    public CRServoGroup forward() {
        for (CRServoEx servo : servos) {
            servo.forward();
        }

        return this;
    }

    public CRServoGroup reverse() {
        for (CRServoEx servo : servos) {
            servo.reverse();
        }

        return this;
    }

    public CRServoGroup setInverted(boolean inverted) {
        for (CRServoEx servo : servos) {
            servo.setInverted(inverted);
        }

        return this;
    }

    public CRServoGroup setInverted(
            int index,
            boolean inverted
    ) {
        get(index).setInverted(inverted);
        return this;
    }

    public CRServoGroup toggleInverted() {
        for (CRServoEx servo : servos) {
            servo.toggleInverted();
        }

        return this;
    }

    public CRServoGroup setDirection(
            DcMotorSimple.Direction direction
    ) {
        for (CRServoEx servo : servos) {
            servo.setDirection(direction);
        }

        return this;
    }

    public CRServoGroup setDirection(
            int index,
            DcMotorSimple.Direction direction
    ) {
        get(index).setDirection(direction);
        return this;
    }

    public CRServoGroup setCachingTolerance(
            double tolerance
    ) {
        for (CRServoEx servo : servos) {
            servo.setCachingTolerance(tolerance);
        }

        return this;
    }

    public CRServoGroup clearCache() {
        for (CRServoEx servo : servos) {
            servo.clearCache();
        }

        return this;
    }

    public CRServoGroup forceSetPower(double power) {
        for (CRServoEx servo : servos) {
            servo.forceSetPower(power);
        }

        return this;
    }

    public double getPower() {
        return leader.getPower();
    }

    public CRServoEx getLeader() {
        return leader;
    }

    public CRServoEx get(int index) {

        if (index < 0 || index >= servos.length) {
            throw new IndexOutOfBoundsException(
                    "CRServo index out of range: " + index
            );
        }

        return servos[index];
    }

    public int size() {
        return servos.length;
    }

    public List<CRServoEx> getServos() {
        return Collections.unmodifiableList(
                Arrays.asList(servos)
        );
    }
}