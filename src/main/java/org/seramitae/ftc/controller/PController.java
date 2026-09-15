package org.seramitae.ftc.controller;

public class PController extends PIDFController {

    public PController(double kP) {
        super(
                kP,
                0,
                0,
                0
        );
    }

    public PController(
            double kP,
            double setPoint
    ) {
        super(
                kP,
                0,
                0,
                0,
                setPoint
        );
    }
}