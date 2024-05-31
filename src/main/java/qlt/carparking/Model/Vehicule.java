package qlt.carparking.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity //Mapea la clase como una entidad en la bd (La entidad es la tabla)
public class Vehicule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Mapea el id y lo genera de manera automatica
    private Long id; //Id de referencia

    private String plate; // Placa del vehículo
    private LocalTime entryTime; //Hora de ingreso
    private boolean wash; // Indica si el vehículo será lavado
    private boolean helmet; // Indica si el casco fue dejado en el parqueadero
    private String type; //Elegir si es (moto o carro)
}
