package org.usfirst.frc.team5938.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import java.lang.Math;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser;
	
	RobotDrive myRobot;
	
	Joystick driveStick;
	Joystick shooterXbox;
	
	Joystick leftXbox;
	Joystick rightXbox;
	
	VictorSP electricSolenoid;
	
	CANTalon intake; //Device ID 1
	CANTalon shooter; //Device ID 2
	CANTalon winch; //Device ID 3
	
	UsbCamera cam;
	UsbCamera cam1;
	
	AHRS ahrs;
	boolean buttonPress;
	int lowerAngle = 50;
	int upperAngle = 75;
	
	public void onCamera() {
		
		cam = CameraServer.getInstance().startAutomaticCapture(0);
		cam.setResolution(160, 120);
		cam.setFPS(20);
		cam1 = CameraServer.getInstance().startAutomaticCapture(1);
		cam1.setResolution(160, 120);
		cam1.setFPS(20); 
		
	}
	
	public void ahrsInit() {
		
		 try {


	          ahrs = new AHRS(SPI.Port.kMXP); 



	      } catch (RuntimeException ex ) {



	          DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);



	      }
		
	
	}
	
	
	public void rotateBot() {
		

		if (lowerAngle <= ahrs.getYaw() && ahrs.getYaw() <= upperAngle){

			myRobot.drive(0, 0);
			buttonPress = false;

		}
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		chooser = new SendableChooser<>();
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		
		SmartDashboard.putData("Auto choices", chooser);
		onCamera();
		ahrsInit();
		
		myRobot = new RobotDrive(0, 1); // class that handles basic drive operations
		
		shooterXbox = new Joystick(0);
		driveStick = new Joystick(1); // set to ID 1 in DriverStation
		intake = new CANTalon(1); 
		shooter = new CANTalon(2);
		winch = new CANTalon (3);
		
		electricSolenoid = new VictorSP(2);
		
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
		autoSelected = chooser.getSelected();
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		myRobot.setSafetyEnabled(true);
		
		while (isOperatorControl() && isEnabled()) {
			
			myRobot.tankDrive(Math.pow(shooterXbox.getRawAxis(5) * .87, 3), Math.pow(shooterXbox.getRawAxis(1) * .87, 3));
			//Cubic acceleration decreases jolting, allowing for smoother driving
			
			
			//myRobot.arcadeDrive(driveStick , 1, driveStick, 2, true);  //Failed Attempt at Arcade Drive
			
			if (shooterXbox.getRawButton(1) == true) {		//A-button controls Winch
				
				winch.set(-1);
			
			} else {
			
				winch.set(0);
			
			}
			
		
			if (driveStick.getRawButton(2)) {

				myRobot.arcadeDrive(.1, .5);
				myRobot.arcadeDrive(.1, -.5);
				rotateBot();

			}
			
			
			if (shooterXbox.getRawAxis(2) >= .25) {		//Left Trigger controls Intake
				
				intake.set(-1);
			
			} else {
			
				intake.set(0);
			
			}
			
		
			if (shooterXbox.getRawAxis(3) >= .25) {		//Right Trigger controls Shooter
				
				shooter.set(.475);
				Timer.delay(.25);						//Gives the flywheel some time to gain speed
				electricSolenoid.set(1);				//Sets the Fuel Gate up
			
			} else {
			
				shooter.set(0);
				electricSolenoid.set(0);
			
			}
			
			
			Timer.delay(0.005); // wait for a motor update time
		}
		
		SmartDashboard.putBoolean(  "IMU_Connected",        ahrs.isConnected());

	    SmartDashboard.putBoolean(  "IMU_IsCalibrating",    ahrs.isCalibrating());

	    SmartDashboard.putNumber(   "IMU_Yaw",              ahrs.getYaw());

		


	    SmartDashboard.putBoolean(  "IMU_IsMoving",         ahrs.isMoving());

	    SmartDashboard.putBoolean(  "IMU_IsRotating",       ahrs.isRotating());

		

	         

	    AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();

	    SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );

	    SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );


	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	

}
