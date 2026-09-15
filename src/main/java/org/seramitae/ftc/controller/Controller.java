package org.seramitae.ftc.controller;

public abstract class Controller {

    protected double setPoint = 0.0;
    protected double measuredValue = 0.0;

    protected double positionError = 0.0;
    protected double velocityError = 0.0;

    protected double previousError = 0.0;

    protected double positionTolerance = 0.05;
    protected double velocityTolerance = Double.POSITIVE_INFINITY;

    protected double minimumOutput = 0.0;

    protected double period = 0.0;

    protected long previousTime = System.nanoTime();

    public double calculate(double measuredValue) {

        this.measuredValue = measuredValue;

        updateErrors();

        double output = calculateOutput(measuredValue);

        if (!atSetPoint()
                && minimumOutput > 0
                && Math.abs(output) < minimumOutput) {

            double sign = Math.signum(output);

            if (sign == 0) {
                sign = Math.signum(positionError);
            }

            output = sign * minimumOutput;
        }

        return output;
    }

    public double calculate(
            double measuredValue,
            double setPoint
    ) {
        setSetPoint(setPoint);
        return calculate(measuredValue);
    }

    public double calculate() {
        return calculate(measuredValue);
    }

    protected void updateErrors() {

        long currentTime = System.nanoTime();

        period =
                (currentTime - previousTime)
                        / 1_000_000_000.0;

        positionError =
                setPoint - measuredValue;

        if (period > 0) {
            velocityError =
                    (positionError - previousError)
                            / period;
        } else {
            velocityError = 0;
        }

        previousError = positionError;
        previousTime = currentTime;
    }

    protected abstract double calculateOutput(
            double measuredValue
    );

    public Controller setSetPoint(double setPoint) {
        this.setPoint = setPoint;
        positionError = setPoint - measuredValue;
        return this;
    }

    public double getSetPoint() {
        return setPoint;
    }

    public boolean atSetPoint() {
        return Math.abs(positionError)
                <= positionTolerance
                &&
                Math.abs(velocityError)
                        <= velocityTolerance;
    }

    public double getPositionError() {
        return positionError;
    }

    public double getVelocityError() {
        return velocityError;
    }

    public Controller setTolerance(
            double positionTolerance
    ) {
        return setTolerance(
                positionTolerance,
                Double.POSITIVE_INFINITY
        );
    }

    public Controller setTolerance(
            double positionTolerance,
            double velocityTolerance
    ) {

        if (positionTolerance < 0
                || velocityTolerance < 0) {

            throw new IllegalArgumentException(
                    "Tolerance cannot be negative"
            );
        }

        this.positionTolerance =
                positionTolerance;

        this.velocityTolerance =
                velocityTolerance;

        return this;
    }

    public double[] getTolerance() {
        return new double[]{
                positionTolerance,
                velocityTolerance
        };
    }

    public Controller setMinimumOutput(
            double minimumOutput
    ) {

        if (minimumOutput < 0) {
            throw new IllegalArgumentException(
                    "Minimum output cannot be negative"
            );
        }

        this.minimumOutput = minimumOutput;

        return this;
    }

    public double getMinimumOutput() {
        return minimumOutput;
    }

    public double getPeriod() {
        return period;
    }

    public void reset() {

        measuredValue = 0;
        positionError = 0;
        velocityError = 0;
        previousError = 0;

        period = 0;

        previousTime = System.nanoTime();

        resetController();
    }

    protected void resetController() {
        // Optional override
    }
}