package org.seramitae.ftc.controller.feedforward;

public class ArmFeedforward {

    private double kS;
    private double kCos;
    private double kV;
    private double kA;

    public ArmFeedforward(
            double kS,
            double kCos,
            double kV
    ) {
        this(
                kS,
                kCos,
                kV,
                0
        );
    }

    public ArmFeedforward(
            double kS,
            double kCos,
            double kV,
            double kA
    ) {
        this.kS = kS;
        this.kCos = kCos;
        this.kV = kV;
        this.kA = kA;
    }

    public double calculate(
            double positionRadians,
            double velocity
    ) {
        return calculate(
                positionRadians,
                velocity,
                0
        );
    }

    public double calculate(
            double positionRadians,
            double velocity,
            double acceleration
    ) {

        return kS * Math.signum(velocity)
                + kCos * Math.cos(positionRadians)
                + kV * velocity
                + kA * acceleration;
    }

    public ArmFeedforward setKS(double value) {
        kS = value;
        return this;
    }

    public ArmFeedforward setKCos(double value) {
        kCos = value;
        return this;
    }

    public ArmFeedforward setKV(double value) {
        kV = value;
        return this;
    }

    public ArmFeedforward setKA(double value) {
        kA = value;
        return this;
    }

    public double getKS() {
        return kS;
    }

    public double getKCos() {
        return kCos;
    }

    public double getKV() {
        return kV;
    }

    public double getKA() {
        return kA;
    }
}