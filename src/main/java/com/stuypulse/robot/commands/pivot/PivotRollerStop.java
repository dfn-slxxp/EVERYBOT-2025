package com.stuypulse.robot.commands.pivot;

import com.stuypulse.robot.constants.Settings;
import com.stuypulse.robot.subsystems.pivot.Pivot;

import edu.wpi.first.wpilibj2.command.InstantCommand;

public class PivotRollerStop extends InstantCommand {
    private Pivot pivot;

    public PivotRollerStop() {
        pivot = Pivot.getInstance();
        addRequirements(pivot);
    }

    public void initialize() {
        pivot.setRollerMotor(0);
    }

}
