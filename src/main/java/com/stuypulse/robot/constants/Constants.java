package com.stuypulse.robot.constants;

import edu.wpi.first.math.geometry.Rotation2d;

/** Miscellaneous constants */
public interface Constants {
    public interface Climb {
        Rotation2d MIN_ANGLE = Rotation2d.kZero;
        Rotation2d MAX_ANGLE = Rotation2d.fromDegrees(87.5);
    }

    public interface Drivetrain {
        public static final double WHEEL_CIRCUMFERENCE = 0.479; //TODO: RECHECK WHEEL CIRCUMFERENCE, SHOULD BE IN INCHES
    }
}
