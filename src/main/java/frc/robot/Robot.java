package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.Elevador;

public class Robot extends TimedRobot {

  public Elevador elevador;
  public XboxController controller;

  // Banderas para movimiento
  private boolean moveToL2 = false;
  private boolean moveToBottom = false;

  @Override
  public void robotInit() {
    elevador = new Elevador();
    controller = new XboxController(0);
  }

  @Override
  public void teleopPeriodic() {
    // Si presiono el botón X, inicio el movimiento hacia L2
    if (controller.getXButton()) {
      moveToL2 = true;
      System.out.println("Comenzando a subir al nivel L2...");
    }

    // Si presiono el botón A, inicio el movimiento hacia abajo (limit switch 0)
    if (controller.getAButton()) {
      moveToBottom = true;
      System.out.println("Comenzando a bajar al nivel inferior...");
    }

    // Movimiento hacia L2
    if (moveToL2) {
      if (elevador.isAtMiddle()) { // seguir subiendo hasta que llegue
        elevador.subir();
        System.out.println("Subiendo elevador hacia L2...");
      } else {
        elevador.detenerse();
        moveToL2 = false;
        System.out.println("Elevador detenido en L2 (switch activado).");
      }
    }

    // Movimiento hacia abajo
    else if (moveToBottom) {
      if (elevador.isAtBottom()) { // seguir bajando hasta que llegue
        elevador.bajar();
        System.out.println("Bajando elevador hacia abajo...");
      } else {
        elevador.detenerse();
        moveToBottom = false;
        System.out.println("Elevador detenido en el límite inferior (switch activado).");
      }
    }

    // Si no hay movimiento pendiente
    else {
      elevador.detenerse();
    }
  }
}
