package org.seramitae.ftc.controller;

public class SquIDController extends Controller {

    protected double kP;

    public SquIDController(double kP) {
        this.kP = kP;
    }

    public SquIDController(
            double kP,
            double setPoint
    ) {
        this(kP);
        setSetPoint(setPoint);
    }

    @Override
    protected double calculateOutput(
            double measuredValue
    ) {

        return kP
                * Math.signum(positionError)
                * Math.sqrt(
                Math.abs(positionError)
        );
    }

    public SquIDController setP(double kP) {
        this.kP = kP;
        return this;
    }

    public double getP() {
        return kP;
    }
}