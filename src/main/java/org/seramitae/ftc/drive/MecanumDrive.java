package org.seramitae.ftc.drive;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.seramitae.ftc.hardware.Motor.Motor;

public class MecanumDrive {

    public enum DriveMode {
        ROBOT_CENTRIC,
        FIELD_CENTRIC
    }

    private final Motor frontLeft;
    private final Motor frontRight;
    private final Motor backLeft;
    private final Motor backRight;

    private IMU imu;

    private DriveMode driveMode =
            DriveMode.ROBOT_CENTRIC;

    private double speedMultiplier = 1.0;
    private double headingOffset = 0.0;

    public MecanumDrive(
            Motor frontLeft,
            Motor frontRight,
            Motor backLeft,
            Motor backRight
    ) {
        validateMotors(
                frontLeft,
                frontRight,
                backLeft,
                backRight
        );

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    public MecanumDrive(
            Motor frontLeft,
            Motor frontRight,
            Motor backLeft,
            Motor backRight,
            IMU imu
    ) {
        this(
                frontLeft,
                frontRight,
                backLeft,
                backRight
        );

        setIMU(imu);
    }

    public MecanumDrive(
            Motor frontLeft,
            Motor frontRight,
            Motor backLeft,
            Motor backRight,
            HardwareMap hardwareMap,
            String imuName
    ) {
        this(
                frontLeft,
                frontRight,
                backLeft,
                backRight,
                hardwareMap.get(
                        IMU.class,
                        imuName
                )
        );
    }

    public MecanumDrive(
            HardwareMap hardwareMap,
            String frontLeftName,
            String frontRightName,
            String backLeftName,
            String backRightName
    ) {
        this(
                new Motor(
                        hardwareMap,
                        frontLeftName
                ),
                new Motor(
                        hardwareMap,
                        frontRightName
                ),
                new Motor(
                        hardwareMap,
                        backLeftName
                ),
                new Motor(
                        hardwareMap,
                        backRightName
                )
        );
    }

    public MecanumDrive(
            HardwareMap hardwareMap,
            String frontLeftName,
            String frontRightName,
            String backLeftName,
            String backRightName,
            String imuName
    ) {
        this(
                hardwareMap,
                frontLeftName,
                frontRightName,
                backLeftName,
                backRightName
        );

        this.imu =
                hardwareMap.get(
                        IMU.class,
                        imuName
                );
    }

    public MecanumDrive initializeIMU(
            RevHubOrientationOnRobot.LogoFacingDirection logo,
            RevHubOrientationOnRobot.UsbFacingDirection usb
    ) {
        requireIMU();

        RevHubOrientationOnRobot orientation =
                new RevHubOrientationOnRobot(
                        logo,
                        usb
                );

        IMU.Parameters parameters =
                new IMU.Parameters(
                        orientation
                );

        imu.initialize(parameters);

        return this;
    }

    public MecanumDrive drive(
            double forward,
            double strafe,
            double turn
    ) {
        if (driveMode ==
                DriveMode.FIELD_CENTRIC) {

            return driveFieldCentric(
                    forward,
                    strafe,
                    turn
            );
        }

        return driveRobotCentric(
                forward,
                strafe,
                turn
        );
    }

    public MecanumDrive driveRobotCentric(
            double forward,
            double strafe,
            double turn
    ) {
        double denominator =
                Math.max(
                        Math.abs(forward)
                                + Math.abs(strafe)
                                + Math.abs(turn),
                        1.0
                );

        double frontLeftPower =
                (forward + strafe + turn)
                        / denominator;

        double frontRightPower =
                (forward - strafe - turn)
                        / denominator;

        double backLeftPower =
                (forward - strafe + turn)
                        / denominator;

        double backRightPower =
                (forward + strafe - turn)
                        / denominator;

        return setMotorPowers(
                frontLeftPower,
                frontRightPower,
                backLeftPower,
                backRightPower
        );
    }

    public MecanumDrive driveFieldCentric(
            double forward,
            double strafe,
            double turn
    ) {
        requireIMU();

        double heading =
                getHeadingRadians();

        double cos =
                Math.cos(-heading);

        double sin =
                Math.sin(-heading);

        double rotatedStrafe =
                strafe * cos
                        - forward * sin;

        double rotatedForward =
                strafe * sin
                        + forward * cos;

        return driveRobotCentric(
                rotatedForward,
                rotatedStrafe,
                turn
        );
    }

    public MecanumDrive setMotorPowers(
            double frontLeftPower,
            double frontRightPower,
            double backLeftPower,
            double backRightPower
    ) {
        frontLeft.setPower(
                clamp(frontLeftPower)
                        * speedMultiplier
        );

        frontRight.setPower(
                clamp(frontRightPower)
                        * speedMultiplier
        );

        backLeft.setPower(
                clamp(backLeftPower)
                        * speedMultiplier
        );

        backRight.setPower(
                clamp(backRightPower)
                        * speedMultiplier
        );

        return this;
    }

    public MecanumDrive stop() {
        frontLeft.stop();
        frontRight.stop();
        backLeft.stop();
        backRight.stop();

        return this;
    }

    public MecanumDrive brake() {
        frontLeft.brake();
        frontRight.brake();
        backLeft.brake();
        backRight.brake();

        return this;
    }

    public MecanumDrive coast() {
        frontLeft.coast();
        frontRight.coast();
        backLeft.coast();
        backRight.coast();

        return this;
    }

    public MecanumDrive setDriveMode(
            DriveMode driveMode
    ) {
        if (driveMode == null) {
            throw new IllegalArgumentException(
                    "DriveMode cannot be null"
            );
        }

        if (driveMode ==
                DriveMode.FIELD_CENTRIC
                && imu == null) {

            throw new IllegalStateException(
                    "Field centric drive requires an IMU"
            );
        }

        this.driveMode = driveMode;

        return this;
    }

    public MecanumDrive fieldCentric() {
        return setDriveMode(
                DriveMode.FIELD_CENTRIC
        );
    }

    public MecanumDrive robotCentric() {
        return setDriveMode(
                DriveMode.ROBOT_CENTRIC
        );
    }

    public MecanumDrive toggleDriveMode() {
        if (isFieldCentric()) {
            return robotCentric();
        }

        return fieldCentric();
    }

    public DriveMode getDriveMode() {
        return driveMode;
    }

    public boolean isFieldCentric() {
        return driveMode ==
                DriveMode.FIELD_CENTRIC;
    }

    public MecanumDrive setSpeedMultiplier(
            double multiplier
    ) {
        speedMultiplier =
                clampPositive(
                        multiplier
                );

        return this;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public MecanumDrive resetHeading() {
        requireIMU();

        headingOffset =
                getRawHeadingRadians();

        return this;
    }

    public MecanumDrive resetYaw() {
        requireIMU();

        imu.resetYaw();

        headingOffset = 0.0;

        return this;
    }

    public MecanumDrive setHeading(
            double heading,
            AngleUnit unit
    ) {
        requireIMU();

        if (unit == null) {
            throw new IllegalArgumentException(
                    "AngleUnit cannot be null"
            );
        }

        double desiredHeading =
                unit == AngleUnit.DEGREES
                        ? Math.toRadians(heading)
                        : heading;

        headingOffset =
                normalizeRadians(
                        getRawHeadingRadians()
                                - desiredHeading
                );

        return this;
    }

    public double getHeadingRadians() {
        requireIMU();

        return normalizeRadians(
                getRawHeadingRadians()
                        - headingOffset
        );
    }

    public double getHeadingDegrees() {
        return Math.toDegrees(
                getHeadingRadians()
        );
    }

    public double getHeading(
            AngleUnit unit
    ) {
        if (unit == null) {
            throw new IllegalArgumentException(
                    "AngleUnit cannot be null"
            );
        }

        if (unit == AngleUnit.DEGREES) {
            return getHeadingDegrees();
        }

        return getHeadingRadians();
    }

    public MecanumDrive setIMU(IMU imu) {
        if (imu == null) {
            throw new IllegalArgumentException(
                    "IMU cannot be null"
            );
        }

        this.imu = imu;

        return this;
    }

    public boolean hasIMU() {
        return imu != null;
    }

    public IMU getIMU() {
        return imu;
    }

    public Motor getFrontLeft() {
        return frontLeft;
    }

    public Motor getFrontRight() {
        return frontRight;
    }

    public Motor getBackLeft() {
        return backLeft;
    }

    public Motor getBackRight() {
        return backRight;
    }

    private double getRawHeadingRadians() {
        requireIMU();

        YawPitchRollAngles angles =
                imu.getRobotYawPitchRollAngles();

        return angles.getYaw(
                AngleUnit.RADIANS
        );
    }

    private void requireIMU() {
        if (imu == null) {
            throw new IllegalStateException(
                    "This operation requires an IMU"
            );
        }
    }

    private static void validateMotors(
            Motor frontLeft,
            Motor frontRight,
            Motor backLeft,
            Motor backRight
    ) {
        if (frontLeft == null
                || frontRight == null
                || backLeft == null
                || backRight == null) {

            throw new IllegalArgumentException(
                    "MecanumDrive motors cannot be null"
            );
        }
    }

    private static double clamp(
            double value
    ) {
        return Math.max(
                -1.0,
                Math.min(
                        1.0,
                        value
                )
        );
    }

    private static double clampPositive(
            double value
    ) {
        return Math.max(
                0.0,
                Math.min(
                        1.0,
                        value
                )
        );
    }

    private static double normalizeRadians(
            double angle
    ) {
        while (angle > Math.PI) {
            angle -= 2.0 * Math.PI;
        }

        while (angle < -Math.PI) {
            angle += 2.0 * Math.PI;
        }

        return angle;
    }
}