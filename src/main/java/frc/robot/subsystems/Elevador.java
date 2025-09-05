package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Elevador extends SubsystemBase {

    public SparkMax elevadorMotorLeader; // Motor principal
    public SparkMax elevadorMotorFollower; // Motor seguidor
    public DigitalInput Limit1; // Abajo
    public DigitalInput Limit2; // Medio
    public DigitalInput Limit3; // Arriba

    public Elevador() {
        elevadorMotorLeader = new SparkMax(5, MotorType.kBrushed);
        elevadorMotorFollower = new SparkMax(6, MotorType.kBrushed);

        Limit1 = new DigitalInput(0); // DIO 0
        Limit2 = new DigitalInput(1); // DIO 1
        Limit3 = new DigitalInput(2); // DIO 2

        // Configuración de motores
        SparkMaxConfig baseConfig = new SparkMaxConfig();
        SparkMaxConfig elevadorMotorFollowerConfig = new SparkMaxConfig();

        baseConfig.idleMode(IdleMode.kBrake);

        elevadorMotorFollowerConfig
                .apply(baseConfig)
                .follow(elevadorMotorLeader);

        elevadorMotorLeader.configure(baseConfig, com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters,
                PersistMode.kNoPersistParameters);
        elevadorMotorFollower.configure(elevadorMotorFollowerConfig,
                com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);
    }

    public void subir() {
        elevadorMotorLeader.set(1.0); // Subir a full
    }

    public void bajar() {
        elevadorMotorLeader.set(-0.5); // Bajar a media velocidad
    }

    public void detenerse() {
        elevadorMotorLeader.set(0.0); // Parar
    }

    public boolean isNotAtBottom() {
        return !Limit1.get(); // switch NO → presionado = true
    }

    public boolean isNotAtMiddle() {
        return !Limit2.get();
    }

    public boolean isNotAtTop() {
        return !Limit3.get();
    }
}
