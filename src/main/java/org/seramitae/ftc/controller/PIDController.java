package org.seramitae.ftc.controller;

public class PIDController extends PIDFController {

    public PIDController(
            double kP,
            double kI,
            double kD
    ) {
        super(
                kP,
                kI,
                kD,
                0
        );
    }

    public PIDController(
            double kP,
            double kI,
            double kD,
            double setPoint
    ) {
        super(
                kP,
                kI,
                kD,
                0,
                setPoint
        );
    }

    public PIDController setPID(
            double kP,
            double kI,
            double kD
    ) {
        setPIDF(
                kP,
                kI,
                kD,
                0
        );

        return this;
    }
}