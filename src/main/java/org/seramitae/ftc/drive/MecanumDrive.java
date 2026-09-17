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

        this.imu = imu;
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

        this.imu = hardwareMap.get(
                IMU.class,
                imuName
        );
    }

    public MecanumDrive initializeIMU(
            RevHubOrientationOnRobot.LogoFacingDirection logo,
            RevHubOrientationOnRobot.UsbFacingDirection usb
    ) {

        if (imu == null) {
            throw new IllegalStateException(
                    "No IMU has been configured"
            );
        }

        RevHubOrientationOnRobot orientation =
                new RevHubOrientationOnRobot(
                        logo,
                        usb
                );

        imu.initialize(
                new IMU.Parameters(
                        orientation
                )
        );

        return this;
    }

    public MecanumDrive drive(
            double forward,
            double strafe,
            double turn
    ) {

        if (driveMode
                == DriveMode.FIELD_CENTRIC) {

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

        double backLeftPower =
                (forward - strafe + turn)
                        / denominator;

        double frontRightPower =
                (forward - strafe - turn)
                        / denominator;

        double backRightPower =
                (forward + strafe - turn)
                        / denominator;

        setMotorPowers(
                frontLeftPower,
                frontRightPower,
                backLeftPower,
                backRightPower
        );

        return this;
    }

    public MecanumDrive driveFieldCentric(
            double forward,
            double strafe,
            double turn
    ) {

        requireIMU();

        double heading =
                getHeadingRadians();

        double rotatedStrafe =
                strafe * Math.cos(-heading)
                        - forward * Math.sin(-heading);

        double rotatedForward =
                strafe * Math.sin(-heading)
                        + forward * Math.cos(-heading);

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

        if (driveMode
                == DriveMode.FIELD_CENTRIC
                && imu == null) {

            throw new IllegalStateException(
                    "Field centric drive requires an IMU"
            );
        }

        this.driveMode = driveMode;

        return this;
    }

    public DriveMode getDriveMode() {
        return driveMode;
    }

    public MecanumDrive toggleDriveMode() {

        if (driveMode
                == DriveMode.ROBOT_CENTRIC) {

            setDriveMode(
                    DriveMode.FIELD_CENTRIC
            );

        } else {

            setDriveMode(
                    DriveMode.ROBOT_CENTRIC
            );
        }

        return this;
    }

    public boolean isFieldCentric() {
        return driveMode
                == DriveMode.FIELD_CENTRIC;
    }

    public MecanumDrive setSpeedMultiplier(
            double multiplier
    ) {

        speedMultiplier =
                clamp(
                        Math.abs(multiplier)
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

        headingOffset = 0;

        return this;
    }

    public MecanumDrive setHeading(
            double heading,
            AngleUnit unit
    ) {

        requireIMU();

        double desiredHeading =
                unit == AngleUnit.DEGREES
                        ? Math.toRadians(heading)
                        : heading;

        headingOffset =
                getRawHeadingRadians()
                        - desiredHeading;

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

    private static double normalizeRadians(
            double angle
    ) {

        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }

        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    private static double clamp(
            double value
    ) {

        return Math.max(
                -1,
                Math.min(
                        1,
                        value
                )
        );
    }
}