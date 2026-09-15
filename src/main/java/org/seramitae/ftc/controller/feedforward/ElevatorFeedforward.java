package org.seramitae.ftc.controller.feedforward;

public class ElevatorFeedforward {

    private double kS;
    private double kG;
    private double kV;
    private double kA;

    public ElevatorFeedforward(
            double kS,
            double kG,
            double kV
    ) {
        this(
                kS,
                kG,
                kV,
                0
        );
    }

    public ElevatorFeedforward(
            double kS,
            double kG,
            double kV,
            double kA
    ) {
        this.kS = kS;
        this.kG = kG;
        this.kV = kV;
        this.kA = kA;
    }

    public double calculate(double velocity) {
        return calculate(
                velocity,
                0
        );
    }

    public double calculate(
            double velocity,
            double acceleration
    ) {

        return kS * Math.signum(velocity)
                + kG
                + kV * velocity
                + kA * acceleration;
    }

    public ElevatorFeedforward setKS(double value) {
        kS = value;
        return this;
    }

    public ElevatorFeedforward setKG(double value) {
        kG = value;
        return this;
    }

    public ElevatorFeedforward setKV(double value) {
        kV = value;
        return this;
    }

    public ElevatorFeedforward setKA(double value) {
        kA = value;
        return this;
    }

    public double getKS() {
        return kS;
    }

    public double getKG() {
        return kG;
    }

    public double getKV() {
        return kV;
    }

    public double getKA() {
        return kA;
    }
}