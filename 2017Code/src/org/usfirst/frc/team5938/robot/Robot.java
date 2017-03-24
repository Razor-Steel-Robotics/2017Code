package org.usfirst.frc.team5938.robot;

import com.ctre.CANTalon;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	RobotDrive myRobot;

	Joystick xboxDrive;
	Joystick xboxShooter;

	CANTalon intake; // Device ID 1
	CANTalon shooter; // Device ID 2
	CANTalon winch; // Device ID 3

	UsbCamera cam;
	UsbCamera cam1;

	Timer timer;

	int autoLoopCounter;
	boolean isFast;// true drive straight; false do nothing

	public void onCamera() {

		cam = CameraServer.getInstance().startAutomaticCapture(0);
		cam.setResolution(160, 120);
		cam.setFPS(20);
		cam1 = CameraServer.getInstance().startAutomaticCapture(1);
		cam1.setResolution(160, 120);
		cam1.setFPS(20);

	}

	@Override
	public void robotInit() {

		timer = new Timer();

		myRobot = new RobotDrive(0, 1); // class that handles basic drive
										// operations

		xboxShooter = new Joystick(1);
		xboxDrive = new Joystick(0); // set to ID 1 in DriverStation
		intake = new CANTalon(1);
		shooter = new CANTalon(2);
		winch = new CANTalon(3);

		isFast = true;
		onCamera();

	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {

		autoLoopCounter = 0;

		timer.reset();
		timer.start();

	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {

		if (timer.get() < 4.6) {// Make slow side faster and fast side slower
								// [Make sure power is the same in the drive
								// logs
			myRobot.drive(0.3, -.000029);

		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		myRobot.setSafetyEnabled(true);

		while (isOperatorControl() && isEnabled()) {

			if (isFast == true) {
				myRobot.tankDrive(xboxDrive.getRawAxis(5) * 1.0,
						xboxDrive.getRawAxis(1) * .95);
			} else {
				myRobot.tankDrive(xboxDrive.getRawAxis(5) * .6,
						xboxDrive.getRawAxis(1) * .6);

			}

			if (xboxShooter.getRawButton(5)) {
				isFast = true;

			}

			if (xboxShooter.getRawButton(6)) {

				isFast = false;
			}

			if (xboxShooter.getRawButton(1) == true) { // A-button
														// controls winch
				winch.set(-1);

			} else {

				winch.set(0);

			}

			if (xboxShooter.getRawAxis(2) >= .25) { // Left Trigger controls
													// intake
				intake.set(-1);

			} else {

				intake.set(0);

			}

			if (xboxShooter.getRawAxis(3) >= .25) { // Right Trigger controls
													// shooter
				shooter.set(.5);

			} else {

				shooter.set(0);

			}

			Timer.delay(.005); // wait for a motor update time

		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

} // UPDATED on 3/24/17 - 7:10PM
