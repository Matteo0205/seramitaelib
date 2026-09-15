package org.seramitae.ftc.controller;

public class PDController extends PIDFController {

    public PDController(
            double kP,
            double kD
    ) {
        super(
                kP,
                0,
                kD,
                0
        );
    }

    public PDController(
            double kP,
            double kD,
            double setPoint
    ) {
        super(
                kP,
                0,
                kD,
                0,
                setPoint
        );
    }

    public PDController setPD(
            double kP,
            double kD
    ) {
        setPIDF(
                kP,
                0,
                kD,
                0
        );

        return this;
    }
}