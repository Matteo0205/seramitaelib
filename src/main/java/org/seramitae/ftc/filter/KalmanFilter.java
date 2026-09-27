package org.seramitae.ftc.filter;

public class KalmanFilter {

    private double estimate;
    private double covariance;

    private double processVariance;
    private double measurementVariance;

    private double gain;

    public KalmanFilter(
            double processVariance,
            double measurementVariance
    ) {
        this(
                0.0,
                1.0,
                processVariance,
                measurementVariance
        );
    }

    public KalmanFilter(
            double initialEstimate,
            double initialCovariance,
            double processVariance,
            double measurementVariance
    ) {
        requireFinite(initialEstimate);
        requireNonnegative(initialCovariance);
        requireNonnegative(processVariance);
        requirePositive(measurementVariance);

        this.estimate = initialEstimate;
        this.covariance = initialCovariance;

        this.processVariance = processVariance;
        this.measurementVariance = measurementVariance;
    }

    public double predict(double stateChange) {

        requireFinite(stateChange);

        estimate += stateChange;
        covariance += processVariance;

        return estimate;
    }

    public double correct(double measurement) {

        requireFinite(measurement);

        gain = covariance
                / (covariance + measurementVariance);

        estimate += gain * (measurement - estimate);

        covariance *= (1.0 - gain);

        return estimate;
    }

    public double update(
            double measurement,
            double stateChange
    ) {

        predict(stateChange);

        return correct(measurement);
    }

    public double update(double measurement) {

        return update(measurement, 0.0);
    }

    public void reset(
            double initialEstimate,
            double initialCovariance
    ) {

        requireFinite(initialEstimate);
        requireNonnegative(initialCovariance);

        estimate = initialEstimate;
        covariance = initialCovariance;

        gain = 0.0;
    }

    public void setProcessVariance(double value) {

        requireNonnegative(value);

        processVariance = value;
    }

    public void setMeasurementVariance(double value) {

        requirePositive(value);

        measurementVariance = value;
    }

    public double getEstimate() {
        return estimate;
    }

    public double getCovariance() {
        return covariance;
    }

    public double getGain() {
        return gain;
    }

    public double getProcessVariance() {
        return processVariance;
    }

    public double getMeasurementVariance() {
        return measurementVariance;
    }

    private static void requireFinite(double value) {

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    "Value must be finite"
            );
        }
    }

    private static void requireNonnegative(double value) {

        requireFinite(value);

        if (value < 0.0) {
            throw new IllegalArgumentException(
                    "Value must be >= 0"
            );
        }
    }

    private static void requirePositive(double value) {

        requireFinite(value);

        if (value <= 0.0) {
            throw new IllegalArgumentException(
                    "Value must be > 0"
            );
        }
    }
}