package org.seramitae.ftc.controller;

public class SquIDFController extends SquIDController {

    private double kF;

    public SquIDFController(
            double kP,
            double kF
    ) {
        super(kP);
        this.kF = kF;
    }

    public SquIDFController(
            double kP,
            double kF,
            double setPoint
    ) {
        super(kP, setPoint);
        this.kF = kF;
    }

    @Override
    protected double calculateOutput(
            double measuredValue
    ) {

        double squid =
                kP
                        * Math.signum(positionError)
                        * Math.sqrt(
                        Math.abs(positionError)
                );

        double feedforward =
                kF * Math.signum(positionError);

        return squid + feedforward;
    }

    public SquIDFController setF(double kF) {
        this.kF = kF;
        return this;
    }

    public double getF() {
        return kF;
    }

    public SquIDFController setSquIDF(
            double kP,
            double kF
    ) {
        this.kP = kP;
        this.kF = kF;

        return this;
    }

    public double[] getCoefficients() {
        return new double[]{
                kP,
                kF
        };
    }
}