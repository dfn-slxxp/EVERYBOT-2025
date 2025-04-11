package com.stuypulse.robot.subsystems.pivot;

import com.revrobotics.spark.SparkLowLevel.MotorType;

import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.stuypulse.robot.constants.Gains;
import com.stuypulse.robot.constants.Motors;
import com.stuypulse.robot.constants.Ports;
import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.util.SysId;

import com.stuypulse.stuylib.network.SmartNumber;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.PubSub;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;

import com.stuypulse.stuylib.control.feedback.PIDController;
import com.stuypulse.stuylib.control.Controller;
import com.stuypulse.stuylib.control.feedforward.MotorFeedforward;
import com.stuypulse.stuylib.control.feedforward.ArmFeedforward;

public class PivotImpl extends Pivot {

    private SparkMax pivotMotor;
    private RelativeEncoder pivotEncoder;

    private Controller controller;
    private SparkMax rollerMotor;

    // double CurrentRollerSetSpeed;
    // double CurrentPivotSetSpeed;
    SmartNumber CurrentRollerSetSpeed = new SmartNumber("CurrentRollerSetSpeed", 0);
    SmartNumber CurrentPivotSetSpeed = new SmartNumber("CurrentPivotSetSpeed", 0);

    public PivotImpl() {
        super();
        pivotMotor = new SparkMax(Ports.Pivot.PIVOT_MOTOR,MotorType.kBrushless);
        rollerMotor = new SparkMax(Ports.Pivot.ROLLER_MOTOR, MotorType.kBrushed);

        Motors.PivotConfig.PIVOT_MOTOR_CONFIG.encoder
            .positionConversionFactor(Settings.Pivot.PIVOT_MOTOR_GEAR_RATIO * Settings.Pivot.PIVOT_MOTOR_REDUCTION_FACTOR);  
        
        pivotMotor.configure(Motors.PivotConfig.PIVOT_MOTOR_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        rollerMotor.configure(Motors.PivotConfig.PIVOT_ROLLER_MOTOR_CONFIG, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
        
        pivotEncoder = pivotMotor.getEncoder();

        controller = new ArmFeedforward(Gains.Pivot.FF.kG)
                .add(new PIDController(Gains.Pivot.PID.kP, Gains.Pivot.PID.kI, Gains.Pivot.PID.kD));
    }
    
    @Override
    public SysIdRoutine getSysIdRoutine() {
        return SysId.getSysIdRoutine(
            pivotMotor.toString(),
            pivotMotor,
            getPivotRotation(),
            Pivot.getInstance(),
            0.5,
            .5,
            10
        );
    }

    @Override
    public void setRollerMotor(double speed) {
        CurrentRollerSetSpeed.set(speed);
        rollerMotor.set(speed);
    }

    @Override
    public void setPivotMotor(double speed) {
        CurrentPivotSetSpeed.set(speed);
        pivotMotor.set(speed);
    }

    public Rotation2d getPivotRotation() {
        return Rotation2d.fromRotations(pivotEncoder.getPosition());
    }

    @Override
    public void ResetPivotEncoder() {
        pivotMotor.getEncoder().setPosition(0);
        pivotEncoder.setPosition(0); //SmartDashboard.putString("Pivot/Successful reset", );
    }

    @Override
    public void setPivotState(PivotState pivotState) { this.pivotState = pivotState; }

    @Override
    public PivotState getPivotState() { return pivotState; }

    @Override
    public void periodic() {
        super.periodic();

        pivotMotor.setVoltage(controller.update(pivotState.targetAngle.getDegrees(), getPivotRotation().getDegrees()));

        SmartDashboard.putNumber("Pivot/Number of Rotations", getPivotRotation().getRotations());
        SmartDashboard.putNumber("Pivot/Current Angle", getPivotRotation().getDegrees());
    }       
}
