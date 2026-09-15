package org.seramitae.ftc.hardware.Motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Motor {

    public enum RunMode {
        RawPower,
        VelocityControl,
        PositionControl
    }

    public enum ZeroPowerBehavior {
        BRAKE,
        FLOAT
    }

    public final DcMotorEx motor;
    public final Encoder encoder;

    private RunMode runMode = RunMode.RawPower;

    private double maxPower = 1.0;

    private double cpr = 1.0;
    private double rpm = 1.0;

    private double positionCoefficient = 0.05;
    private double positionTolerance = 5.0;

    private double velocityKP = 0.0;
    private double velocityKI = 0.0;
    private double velocityKD = 0.0;

    private double kS = 0.0;
    private double kV = 0.0;
    private double kA = 0.0;

    private double velocityIntegral = 0.0;
    private double previousVelocityError = 0.0;

    private long previousVelocityTime = System.nanoTime();

    private int targetPosition = 0;

    public Motor(HardwareMap hardwareMap, String name) {
        motor = hardwareMap.get(DcMotorEx.class, name);
        encoder = new Encoder(motor);
    }

    public Motor(
            HardwareMap hardwareMap,
            String name,
            double cpr,
            double rpm
    ) {
        this(hardwareMap, name);

        if (cpr <= 0) {
            throw new IllegalArgumentException("CPR must be greater than 0");
        }

        if (rpm <= 0) {
            throw new IllegalArgumentException("RPM must be greater than 0");
        }

        this.cpr = cpr;
        this.rpm = rpm;
    }

    public Motor set(double output) {
        output = clamp(output, -1.0, 1.0);

        switch (runMode) {

            case RawPower:
                setPower(output);
                break;

            case PositionControl:
                updatePositionControl(output);
                break;

            case VelocityControl:
                updateVelocityControl(output);
                break;
        }

        return this;
    }

    private void updatePositionControl(double maxOutput) {

        double error = targetPosition - getCurrentPosition();

        if (Math.abs(error) <= positionTolerance) {
            setPower(0);
            return;
        }

        double output = positionCoefficient * error;

        double limit = Math.abs(maxOutput);

        output = clamp(
                output,
                -limit,
                limit
        );

        setPower(output);
    }

    private void updateVelocityControl(double percentage) {

        double targetVelocity =
                percentage * getMaxTicksPerSecond();

        double currentVelocity =
                getCorrectedVelocity();

        double error =
                targetVelocity - currentVelocity;

        long now = System.nanoTime();

        double dt =
                (now - previousVelocityTime)
                        / 1_000_000_000.0;

        if (dt <= 0) {
            dt = 0.001;
        }

        velocityIntegral += error * dt;

        double derivative =
                (error - previousVelocityError) / dt;

        double pid =
                velocityKP * error
                        + velocityKI * velocityIntegral
                        + velocityKD * derivative;

        double feedforward = 0;

        if (targetVelocity != 0) {
            feedforward =
                    kS * Math.signum(targetVelocity)
                            + kV * targetVelocity;
        }

        double output =
                pid + feedforward;

        setPower(output);

        previousVelocityError = error;
        previousVelocityTime = now;
    }

    public Motor setPower(double power) {

        if (Double.isNaN(power)) {
            throw new IllegalArgumentException(
                    "Motor power must not be NaN"
            );
        }

        motor.setPower(
                clamp(
                        power,
                        -maxPower,
                        maxPower
                )
        );

        return this;
    }

    public Motor stopMotor() {
        return setPower(0);
    }

    public Motor stop() {
        return stopMotor();
    }

    public Motor setRunMode(RunMode mode) {

        if (mode == null) {
            throw new IllegalArgumentException(
                    "RunMode cannot be null"
            );
        }

        runMode = mode;

        velocityIntegral = 0;
        previousVelocityError = 0;
        previousVelocityTime = System.nanoTime();

        return this;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public Motor setPositionCoefficient(double coefficient) {

        if (coefficient < 0) {
            throw new IllegalArgumentException(
                    "Position coefficient cannot be negative"
            );
        }

        positionCoefficient = coefficient;

        return this;
    }

    public double getPositionCoefficient() {
        return positionCoefficient;
    }

    public Motor setPositionTolerance(double tolerance) {

        if (tolerance < 0) {
            throw new IllegalArgumentException(
                    "Position tolerance cannot be negative"
            );
        }

        positionTolerance = tolerance;

        return this;
    }

    public double getPositionTolerance() {
        return positionTolerance;
    }

    public Motor setTargetPosition(int ticks) {
        targetPosition = ticks;
        return this;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public boolean atTargetPosition() {
        return Math.abs(
                targetPosition - getCurrentPosition()
        ) <= positionTolerance;
    }

    public Motor setTargetDistance(double distance) {

        if (encoder.getDistancePerPulse() == 0) {
            throw new IllegalStateException(
                    "Distance per pulse must be configured first"
            );
        }

        targetPosition =
                (int) Math.round(
                        distance
                                / encoder.getDistancePerPulse()
                );

        return this;
    }

    public Motor setVeloCoefficients(
            double kP,
            double kI,
            double kD
    ) {
        velocityKP = kP;
        velocityKI = kI;
        velocityKD = kD;

        return this;
    }

    public double[] getVeloCoefficients() {
        return new double[]{
                velocityKP,
                velocityKI,
                velocityKD
        };
    }

    public Motor setFeedforwardCoefficients(
            double kS,
            double kV
    ) {
        return setFeedforwardCoefficients(
                kS,
                kV,
                0
        );
    }

    public Motor setFeedforwardCoefficients(
            double kS,
            double kV,
            double kA
    ) {
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;

        return this;
    }

    public double[] getFeedforwardCoefficients() {
        return new double[]{
                kS,
                kV,
                kA
        };
    }

    public Motor setDistancePerPulse(double distancePerPulse) {
        encoder.setDistancePerPulse(distancePerPulse);
        return this;
    }

    public double getDistance() {
        return encoder.getDistance();
    }

    public int getCurrentPosition() {
        return encoder.getPosition();
    }

    public int getPosition() {
        return getCurrentPosition();
    }

    public double getVelocity() {
        return encoder.getVelocity();
    }

    public double getCorrectedVelocity() {
        return encoder.getCorrectedVelocity();
    }

    public Motor resetEncoder() {
        encoder.reset();
        return this;
    }

    public Motor stopAndResetEncoder() {

        motor.setMode(
                DcMotor.RunMode.STOP_AND_RESET_ENCODER
        );

        motor.setMode(
                DcMotor.RunMode.RUN_WITHOUT_ENCODER
        );

        encoder.resetOffset();

        return this;
    }

    public Motor setDirection(
            DcMotorSimple.Direction direction
    ) {
        motor.setDirection(direction);
        return this;
    }

    public Motor setInverted(boolean inverted) {

        motor.setDirection(
                inverted
                        ? DcMotorSimple.Direction.REVERSE
                        : DcMotorSimple.Direction.FORWARD
        );

        return this;
    }

    public boolean getInverted() {
        return motor.getDirection()
                == DcMotorSimple.Direction.REVERSE;
    }

    public boolean isInverted() {
        return getInverted();
    }

    public Motor reverse() {
        return setInverted(true);
    }

    public Motor forward() {
        return setInverted(false);
    }

    public Motor setZeroPowerBehavior(
            ZeroPowerBehavior behavior
    ) {

        motor.setZeroPowerBehavior(
                behavior == ZeroPowerBehavior.BRAKE
                        ? DcMotor.ZeroPowerBehavior.BRAKE
                        : DcMotor.ZeroPowerBehavior.FLOAT
        );

        return this;
    }

    public Motor setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior behavior
    ) {
        motor.setZeroPowerBehavior(behavior);
        return this;
    }

    public Motor brake() {
        return setZeroPowerBehavior(
                ZeroPowerBehavior.BRAKE
        );
    }

    public Motor coast() {
        return setZeroPowerBehavior(
                ZeroPowerBehavior.FLOAT
        );
    }

    public Motor setMaxPower(double maxPower) {

        this.maxPower =
                clamp(
                        Math.abs(maxPower),
                        0,
                        1
                );

        return this;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public double getCPR() {
        return cpr;
    }

    public double getRPM() {
        return rpm;
    }

    public double getMaxTicksPerSecond() {
        return cpr * rpm / 60.0;
    }

    public double getPower() {
        return motor.getPower();
    }

    public boolean isBusy() {
        return !atTargetPosition();
    }

    public DcMotorEx raw() {
        return motor;
    }

    private static double clamp(
            double value,
            double minimum,
            double maximum
    ) {

        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(
                    "Value must not be NaN"
            );
        }

        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }

    public class Encoder {

        private final DcMotorEx motor;

        private int offset = 0;

        private double distancePerPulse = 1.0;

        private double previousVelocity = 0;

        private Encoder(DcMotorEx motor) {
            this.motor = motor;
        }

        public int getPosition() {
            return motor.getCurrentPosition() - offset;
        }

        public void reset() {
            offset = motor.getCurrentPosition();
        }

        private void resetOffset() {
            offset = 0;
        }

        public double getVelocity() {
            return motor.getVelocity();
        }

        public double getCorrectedVelocity() {

            double velocity = motor.getVelocity();

            double estimate =
                    inverseOverflow(
                            velocity,
                            previousVelocity
                    );

            previousVelocity = estimate;

            return estimate;
        }

        public double getRevolutions() {

            if (cpr == 0) {
                return 0;
            }

            return getPosition() / cpr;
        }

        public Encoder setDistancePerPulse(
                double distancePerPulse
        ) {

            this.distancePerPulse =
                    distancePerPulse;

            return this;
        }

        public double getDistancePerPulse() {
            return distancePerPulse;
        }

        public double getDistance() {
            return getPosition()
                    * distancePerPulse;
        }

        private double inverseOverflow(
                double input,
                double estimate
        ) {

            double real = input;

            while (
                    Math.abs(
                            estimate - real
                    ) > 32768
            ) {

                real += Math.signum(
                        estimate - real
                ) * 65536;
            }

            return real;
        }
    }
}