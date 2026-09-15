package org.seramitae.ftc.controller;

public class PIDFController extends Controller {

    protected double kP;
    protected double kI;
    protected double kD;
    protected double kF;

    protected double integral = 0.0;

    protected double integralMin =
            Double.NEGATIVE_INFINITY;

    protected double integralMax =
            Double.POSITIVE_INFINITY;

    public PIDFController(
            double kP,
            double kI,
            double kD,
            double kF
    ) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public PIDFController(
            double kP,
            double kI,
            double kD,
            double kF,
            double setPoint
    ) {
        this(kP, kI, kD, kF);
        setSetPoint(setPoint);
    }

    @Override
    protected double calculateOutput(
            double measuredValue
    ) {

        if (period > 0) {
            integral += positionError * period;
        }

        integral = clamp(
                integral,
                integralMin,
                integralMax
        );

        return kP * positionError
                + kI * integral
                + kD * velocityError
                + kF * setPoint;
    }

    public PIDFController setP(double kP) {
        this.kP = kP;
        return this;
    }

    public PIDFController setI(double kI) {
        this.kI = kI;
        return this;
    }

    public PIDFController setD(double kD) {
        this.kD = kD;
        return this;
    }

    public PIDFController setF(double kF) {
        this.kF = kF;
        return this;
    }

    public PIDFController setPIDF(
            double kP,
            double kI,
            double kD,
            double kF
    ) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;

        return this;
    }

    public double getP() {
        return kP;
    }

    public double getI() {
        return kI;
    }

    public double getD() {
        return kD;
    }

    public double getF() {
        return kF;
    }

    public double[] getCoefficients() {
        return new double[]{
                kP,
                kI,
                kD,
                kF
        };
    }

    public PIDFController setIntegralBounds(
            double minimum,
            double maximum
    ) {

        if (minimum > maximum) {
            throw new IllegalArgumentException(
                    "Minimum integral bound cannot be greater than maximum"
            );
        }

        integralMin = minimum;
        integralMax = maximum;

        return this;
    }

    public double getIntegral() {
        return integral;
    }

    @Override
    protected void resetController() {
        integral = 0;
    }

    private static double clamp(
            double value,
            double min,
            double max
    ) {
        return Math.max(
                min,
                Math.min(max, value)
        );
    }
}