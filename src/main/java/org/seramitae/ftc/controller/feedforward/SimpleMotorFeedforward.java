package org.seramitae.ftc.controller.feedforward;

public class SimpleMotorFeedforward {

    private double kS;
    private double kV;
    private double kA;

    public SimpleMotorFeedforward(
            double kS,
            double kV
    ) {
        this(
                kS,
                kV,
                0
        );
    }

    public SimpleMotorFeedforward(
            double kS,
            double kV,
            double kA
    ) {
        this.kS = kS;
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

        if (velocity == 0
                && acceleration == 0) {
            return 0;
        }

        return kS * Math.signum(velocity)
                + kV * velocity
                + kA * acceleration;
    }

    public double maxAchievableVelocity(
            double maxVoltage,
            double acceleration
    ) {

        return (
                maxVoltage
                        - kS
                        - kA * acceleration
        ) / kV;
    }

    public double minAchievableVelocity(
            double maxVoltage,
            double acceleration
    ) {

        return (
                -maxVoltage
                        + kS
                        - kA * acceleration
        ) / kV;
    }

    public double maxAchievableAcceleration(
            double maxVoltage,
            double velocity
    ) {

        return (
                maxVoltage
                        - kS * Math.signum(velocity)
                        - kV * velocity
        ) / kA;
    }

    public double minAchievableAcceleration(
            double maxVoltage,
            double velocity
    ) {

        return maxAchievableAcceleration(
                -maxVoltage,
                velocity
        );
    }

    public SimpleMotorFeedforward setKS(double kS) {
        this.kS = kS;
        return this;
    }

    public SimpleMotorFeedforward setKV(double kV) {
        this.kV = kV;
        return this;
    }

    public SimpleMotorFeedforward setKA(double kA) {
        this.kA = kA;
        return this;
    }

    public double getKS() {
        return kS;
    }

    public double getKV() {
        return kV;
    }

    public double getKA() {
        return kA;
    }
}