package qlt.carparking.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qlt.carparking.Model.Vehicule;
import qlt.carparking.Repository.VehiculeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    // Listar todos los vehiculos
    public List<Vehicule> listAll() {
        return vehiculeRepository.findAll();
    }

    // Método para agregar un vehiculo al parqueadero
    public String agregarVehiculo(Vehicule vehicule) {
        if (vehicule.getType().equals("moto") && contarMotos() < 5) {
            vehiculeRepository.save(vehicule);
            return "Moto agregada correctamente";
        } else if (vehicule.getType().equals("carro") && contarCarros() < 10) {
            vehiculeRepository.save(vehicule);
            return "Carro agregado correctamente";
        } else {
            return "El parqueadero está lleno para el tipo de vehiculo";
        }
    }

    // Método para editar un vehiculo
    public String editarVehiculo(Vehicule vehicule) {
        Optional<Vehicule> existingVehicule = vehiculeRepository.findById(vehicule.getId());
        if (existingVehicule.isPresent()) {
            vehiculeRepository.save(vehicule);
            return "Vehiculo editado correctamente";
        } else {
            return "Vehiculo no encontrado";
        }
    }

    // Método para verificar si el parqueadero está lleno
    public boolean parqueaderoLleno() {
        return contarMotos() >= 5 && contarCarros() >= 10;
    }

    // Método para contar las motos en el parqueadero
    private long contarMotos() {
        return vehiculeRepository.findAll().stream().filter(v -> v.getType().equals("moto")).count();
    }

    // Método para contar los carros en el parqueadero
    private long contarCarros() {
        return vehiculeRepository.findAll().stream().filter(v -> v.getType().equals("carro")).count();
    }

    // Método para validar si la placa ya está registrada
    public boolean placaRegistrada(String plate) {
        return vehiculeRepository.findByPlate(plate) != null;
    }

    // Método para validar el formato de la placa de una moto
    public boolean isValidPlateFormatMotorcycle(String plate) {
        return plate.matches("[a-zA-Z]{3}\\d{2}[a-hA-H]{1}");
    }

    // Método para validar el formato de la placa de un carro
    public boolean isValidPlateFormatCar(String plate) {
        return plate.matches("[a-zA-Z]{3}\\d{3}");
    }

    // Método para validar el pico y placa de una moto
    public boolean validarPicoPlacaMotocycle(String plate) {
        char digitoPicoPlaca = plate.charAt(3); // Obtiene el cuarto dígito de la placa
        return !(digitoPicoPlaca == '1' || digitoPicoPlaca == '4');
    }

    // Método para validar el pico y placa de un carro
    public boolean validarPicoPlacaCar(String plate) {
        char digitoPicoPlaca = plate.charAt(5); // Obtiene el cuarto dígito de la placa
        return !(digitoPicoPlaca == '1' || digitoPicoPlaca == '4');
    }

    // Método para obtener un vehiculo por su ID
    public Vehicule get(Long id) {
        return vehiculeRepository.findById(id).orElse(null);
    }

    // Método para guardar un vehiculo
    public void save(Vehicule vehicule) {
        vehiculeRepository.save(vehicule);
    }

    // Método para eliminar un vehiculo por su ID
    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }

    // Calcular el costo del parqueadero
    public double calcularCosto(Vehicule vehicule, long horas) {
        double costo = 0;
        if (vehicule.getType().equals("moto")) {
            costo = horas * 3000;
            if (vehicule.isHelmet()) {
                costo += 2000;
            }
        } else if (vehicule.getType().equals("carro")) {
            costo = horas * 8000;
            if (vehicule.isWash()) {
                costo += 30000;
            }
        }
        return costo;
    }
}