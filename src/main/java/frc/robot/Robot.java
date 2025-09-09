package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevador;
import frc.robot.subsystems.Shooter;

public class Robot extends TimedRobot {

  public Drivetrain drivetrain;
  public Shooter shooter;
  public Elevador elevador;
  public XboxController controller;
  public double startTime;

  // Banderas para movimiento
  private boolean moveToL3 = false;
  private boolean moveToL2 = false;
  private boolean moveToBottom = false;
  private boolean Estop = false;

  // Control de velocidad del drivetrain
  private double drivelimit = 1.0;
  private int lastPOV = -1; // para detectar cambios en la cruceta
  private boolean lastRightTriggerPressed = false;

  public Robot() {
  }

  @Override
  public void robotInit() {
    drivetrain = new Drivetrain();
    elevador = new Elevador();
    shooter = new Shooter();
    controller = new XboxController(0);
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    startTime = Timer.getFPGATimestamp(); // Guarda el tiempo en el que inició autonomo
  }

  @Override
  public void autonomousPeriodic() {
    double timeElapsed = Timer.getFPGATimestamp() - startTime;

    if (timeElapsed < 3.0) {
      drivetrain.drive(-0.5, 0); // Avanza hacia adelante por 3 segundos
    } else {
      drivetrain.drive(0, 0); // Se detiene después
    }
  }

  @Override
  public void teleopInit() {
    if (Estop) {
      moveToBottom = false;
      moveToL2 = false;
      moveToL3 = false;
      drivelimit = 0.0;
    }
  }

  @Override
  public void teleopPeriodic() {

    // ----- TOGGLE con la cruceta hacia arriba -----
    int currentPOV = controller.getPOV();

    if (currentPOV == 0 && lastPOV != 0) {
      // Toggle entre 1.0 y 0.5
      if (drivelimit == 1.0) {
        drivelimit = 0.5;
      } else {
        drivelimit = 1.0;
      }
      System.out.println("driveLimit ahora es " + drivelimit);
    }
    // ----- EMERGENCY STOP con la cruceta hacia abajo -----
    if (currentPOV == 180 && lastPOV != 180) {
      Estop = !Estop; // cambia de true a false o de false a true
      System.out.println("!!!!EMERGENCY STOP " + (Estop ? "ACTIVADO" : "DESACTIVADO") + "!!!");
    }

    lastPOV = currentPOV; // actualizar el último POV

    // Actualizar último POV
    lastPOV = currentPOV;

    lastPOV = currentPOV; // actualizar estado

    // Usar drivelimit en el drivetrain
    drivetrain.drive(controller.getRightY() * drivelimit, controller.getLeftX() * drivelimit);

    // --------- Elevador ---------
    if (controller.getYButton()) {
      moveToL3 = true;
      System.out.println("Comenzando a subir al nivel L3...");
    }

    if (controller.getXButton()) {
      moveToL2 = true;
      System.out.println("Comenzando a subir al nivel L2...");
    }

    if (controller.getAButton()) {
      moveToBottom = true;
      System.out.println("Comenzando a bajar al nivel inferior...");
    }

    if (moveToL3) {
      if (elevador.isNotAtTop()) {
        elevador.subir();
        System.out.println("Subiendo elevador hacia L3...");
      } else {
        elevador.detenerse();
        moveToL3 = false;
        System.out.println("Elevador detenido en L3.");
      }
    }

    if (moveToL2) {
      if (elevador.isNotAtMiddle()) {
        elevador.subir();
        System.out.println("Subiendo elevador hacia L2...");
      } else {
        elevador.detenerse();
        moveToL2 = false;
        System.out.println("Elevador detenido en L2.");
      }
    }

    else if (moveToBottom) {
      if (elevador.isNotAtBottom()) {
        elevador.bajar();
        System.out.println("Bajando elevador...");
      } else {
        elevador.detenerse();
        moveToBottom = false;
        System.out.println("Elevador detenido en el límite inferior.");
      }
    }

    else {
      elevador.detenerse();
    }

    // --------- Shooter ---------
    double rightTrigger = controller.getRawAxis(3); // Shoot
    double leftTrigger = controller.getRawAxis(2); // Reversa

    boolean rightTriggerPressed = rightTrigger > 0.05;

    if (rightTriggerPressed) {
      shooter.shoot(rightTrigger);
    } else if (leftTrigger > 0.05) {
      shooter.reverse(leftTrigger);
    } else {
      shooter.stop();
    }

    // ----- detectar soltar el right trigger -----
    if (lastRightTriggerPressed && !rightTriggerPressed) {
      // Se soltó el trigger = bajar elevador
      moveToBottom = true;
      System.out.println("Trigger derecho soltado, bajando elevador...");
    }

    lastRightTriggerPressed = rightTriggerPressed; // actualizar estado
  }
}
