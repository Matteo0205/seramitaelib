package org.seramitae.ftc.motion;

public class MotionProfile {

    public static final class State {

        public final double position;
        public final double velocity;
        public final double acceleration;

        public State(
                double position,
                double velocity,
                double acceleration
        ) {
            this.position = position;
            this.velocity = velocity;
            this.acceleration = acceleration;
        }
    }

    private final double start;
    private final double goal;

    private final double direction;
    private final double distance;

    private final double acceleration;
    private final double deceleration;

    private final double peakVelocity;

    private final double accelerationTime;
    private final double cruiseTime;
    private final double decelerationTime;

    private final double accelerationDistance;
    private final double cruiseDistance;

    private final double totalTime;

    public MotionProfile(
            double start,
            double goal,
            double maxVelocity,
            double maxAcceleration
    ) {
        this(
                start,
                goal,
                maxVelocity,
                maxAcceleration,
                maxAcceleration
        );
    }

    public MotionProfile(
            double start,
            double goal,
            double maxVelocity,
            double maxAcceleration,
            double maxDeceleration
    ) {

        requireFinite(start);
        requireFinite(goal);

        requirePositive(maxVelocity);
        requirePositive(maxAcceleration);
        requirePositive(maxDeceleration);

        this.start = start;
        this.goal = goal;

        direction = goal >= start ? 1.0 : -1.0;

        distance = Math.abs(goal - start);

        acceleration = maxAcceleration;
        deceleration = maxDeceleration;

        if (distance == 0.0) {

            peakVelocity = 0.0;

            accelerationTime = 0.0;
            cruiseTime = 0.0;
            decelerationTime = 0.0;

            accelerationDistance = 0.0;
            cruiseDistance = 0.0;

            totalTime = 0.0;

            return;
        }

        double distanceToReachMax =
                maxVelocity * maxVelocity
                        / (2.0 * acceleration)
                        + maxVelocity * maxVelocity
                        / (2.0 * deceleration);

        peakVelocity =
                distance >= distanceToReachMax
                        ? maxVelocity
                        : Math.sqrt(
                        2.0 * distance
                        * acceleration
                        * deceleration
                        / (acceleration + deceleration)
                );

        accelerationTime =
                peakVelocity / acceleration;

        decelerationTime =
                peakVelocity / deceleration;

        accelerationDistance =
                peakVelocity * peakVelocity
                        / (2.0 * acceleration);

        double decelerationDistance =
                peakVelocity * peakVelocity
                        / (2.0 * deceleration);

        cruiseDistance =
                Math.max(
                        0.0,
                        distance
                                - accelerationDistance
                                - decelerationDistance
                );

        cruiseTime =
                cruiseDistance / peakVelocity;

        totalTime =
                accelerationTime
                        + cruiseTime
                        + decelerationTime;
    }

    public State calculate(double timeSeconds) {

        requireFinite(timeSeconds);

        if (distance == 0.0) {
            return new State(goal, 0.0, 0.0);
        }

        if (timeSeconds <= 0.0) {
            return new State(start, 0.0, 0.0);
        }

        if (timeSeconds >= totalTime) {
            return new State(goal, 0.0, 0.0);
        }

        double x;
        double v;
        double a;

        if (timeSeconds < accelerationTime) {

            x = 0.5
                    * acceleration
                    * timeSeconds
                    * timeSeconds;

            v = acceleration * timeSeconds;

            a = acceleration;

        } else if (
                timeSeconds < accelerationTime + cruiseTime
        ) {

            double t =
                    timeSeconds - accelerationTime;

            x = accelerationDistance
                    + peakVelocity * t;

            v = peakVelocity;

            a = 0.0;

        } else {

            double t =
                    timeSeconds
                            - accelerationTime
                            - cruiseTime;

            x = accelerationDistance
                    + cruiseDistance
                    + peakVelocity * t
                    - 0.5 * deceleration * t * t;

            v = Math.max(
                    0.0,
                    peakVelocity - deceleration * t
            );

            a = -deceleration;
        }

        return new State(
                start + direction * x,
                direction * v,
                direction * a
        );
    }

    public double getPosition(double timeSeconds) {
        return calculate(timeSeconds).position;
    }

    public double getVelocity(double timeSeconds) {
        return calculate(timeSeconds).velocity;
    }

    public double getAcceleration(double timeSeconds) {
        return calculate(timeSeconds).acceleration;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getPeakVelocity() {
        return peakVelocity;
    }

    public boolean isFinished(double timeSeconds) {
        return timeSeconds >= totalTime;
    }

    public double getStart() {
        return start;
    }

    public double getGoal() {
        return goal;
    }

    private static void requireFinite(double value) {

        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(
                    "Value must be finite"
            );
        }
    }

    private static void requirePositive(double value) {

        requireFinite(value);

        if (value <= 0.0) {
            throw new IllegalArgumentException(
                    "Constraint must be > 0"
            );
        }
    }
}