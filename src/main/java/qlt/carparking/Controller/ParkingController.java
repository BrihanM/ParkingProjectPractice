package qlt.carparking.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import qlt.carparking.Model.Vehicule;
import qlt.carparking.Repository.VehiculeRepository;
import qlt.carparking.Service.ParkingService;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Controller
@CrossOrigin
@RequestMapping("/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService; // Inyecta el servicio de parking
    @Autowired
    private VehiculeRepository vehiculeRepository; // Inyecta el repositorio de vehículos

    // Muestra la página principal del parqueadero
    @GetMapping
    public String showParkingPage(Model model) {
        model.addAttribute("vehicule", new Vehicule()); // Añade un nuevo objeto Vehicule al modelo
        model.addAttribute("vehicules", parkingService.listAll()); // Añade la lista de vehículos al modelo
        return "index"; // Devuelve la vista "index"
    }

    // Devuelve una lista de todos los vehículos en formato JSON
    @GetMapping("/list")
    @ResponseBody
    public List<Vehicule> listAll() {
        return parkingService.listAll(); // Llama al servicio para obtener la lista de vehículos
    }

    // Agrega un nuevo vehículo al parqueadero
    @PostMapping("/add")
    public String addVehicule(@ModelAttribute Vehicule vehicule) {
        // Validar si la placa ya está registrada
        if (parkingService.placaRegistrada(vehicule.getPlate())) {
            return "redirect:/parking?error=placaRegistrada"; // Redirige con un mensaje de error
        }

        // Validar si el parqueadero está lleno
        if (parkingService.parqueaderoLleno()) {
            return "redirect:/parking?error=parqueaderoLleno"; // Redirige con un mensaje de error
        }

        // Validar el formato de la placa y pico y placa según el tipo de vehículo
        if (vehicule.getType().equals("moto")) {
            if (!parkingService.isValidPlateFormatMotorcycle(vehicule.getPlate())) {
                return "redirect:/parking?error=formatoPlacaInvalido"; // Redirige con un mensaje de error
            }
            if (!parkingService.validarPicoPlacaMotocycle(vehicule.getPlate())) {
                return "redirect:/parking?error=picoPlaca"; // Redirige con un mensaje de error
            }
        } else if (vehicule.getType().equals("carro")) {
            if (!parkingService.isValidPlateFormatCar(vehicule.getPlate())) {
                return "redirect:/parking?error=formatoPlacaInvalido"; // Redirige con un mensaje de error
            }
            if (!parkingService.validarPicoPlacaCar(vehicule.getPlate())) {
                return "redirect:/parking?error=picoPlaca"; // Redirige con un mensaje de error
            }
        }

        // Agregar el vehículo al parqueadero
        String resultado = parkingService.agregarVehiculo(vehicule);
        if (resultado.contains("lleno")) {
            return "redirect:/parking?error=parqueaderoLleno"; // Redirige con un mensaje de error
        }

        return "redirect:/parking?success"; // Redirige con un mensaje de éxito
    }

    // Devuelve un vehículo específico en formato JSON
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Vehicule> getVehicule(@PathVariable Long id) {
        Vehicule vehicule = parkingService.get(id); // Obtiene el vehículo por ID
        if (vehicule != null) {
            return ResponseEntity.ok().body(vehicule); // Devuelve el vehículo si se encuentra
        } else {
            return ResponseEntity.notFound().build(); // Devuelve un 404 si no se encuentra
        }
    }

    // Muestra el formulario de edición para un vehículo específico
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Vehicule vehicule = parkingService.get(id); // Obtiene el vehículo por ID
        if (vehicule != null) {
            model.addAttribute("vehicule", vehicule); // Añade el vehículo al modelo
            return "edit"; // Devuelve la vista "edit"
        } else {
            return "redirect:/parking?error=vehiculoNoEncontrado"; // Redirige con un mensaje de error
        }
    }

    // Edita un vehículo existente
    @PostMapping("/edit/{id}")
    public String editarVehiculo(@PathVariable Long id, @ModelAttribute Vehicule vehiculoEditado) {
        Vehicule existingVehicule = parkingService.get(id); // Obtiene el vehículo por ID
        if (existingVehicule != null) {
            if (vehiculoEditado.getPlate() != null && !vehiculoEditado.getPlate().isEmpty()) {
                existingVehicule.setPlate(vehiculoEditado.getPlate()); // Actualiza la placa
            }
            if (vehiculoEditado.getEntryTime() != null) {
                existingVehicule.setEntryTime(vehiculoEditado.getEntryTime()); // Actualiza la hora de ingreso
            }
            if (vehiculoEditado.getType() != null && !vehiculoEditado.getType().isEmpty()) {
                existingVehicule.setType(vehiculoEditado.getType()); // Actualiza el tipo de vehículo
            }
            existingVehicule.setHelmet(vehiculoEditado.isHelmet()); // Actualiza el estado del casco
            existingVehicule.setWash(vehiculoEditado.isWash()); // Actualiza el estado del lavado

            parkingService.save(existingVehicule); // Guarda el vehículo actualizado
            return "redirect:/parking?success"; // Redirige con un mensaje de éxito
        } else {
            return "redirect:/parking?error=vehiculoNoEncontrado"; // Redirige con un mensaje de error
        }
    }

    // Elimina un vehículo por ID
    @PostMapping("/delete/{id}")
    public String deleteVehicule(@PathVariable Long id) {
        parkingService.delete(id); // Llama al servicio para eliminar el vehículo
        return "redirect:/parking"; // Redirige a la página principal
    }

    // Calcula el costo del parqueadero para un vehículo específico
    @GetMapping("/charge/{id}")
    @ResponseBody
    public ResponseEntity<?> chargeVehicule(@PathVariable Long id) {
        Vehicule vehicule = parkingService.get(id); // Obtiene el vehículo por ID
        if (vehicule != null) {
            LocalTime entryTime = vehicule.getEntryTime(); // Obtiene la hora de ingreso
            LocalTime now = LocalTime.now(); // Obtiene la hora actual
            long minutos = Duration.between(entryTime, now).toMinutes(); // Calcula la duración en minutos
            long horas = (minutos + 59) / 60; // Calcula las horas, redondeando hacia arriba si hay fracción de hora
            double costo = parkingService.calcularCosto(vehicule, horas); // Calcula el costo
            return ResponseEntity.ok().body(new ResponseMessage("El costo del parqueadero es: " + costo)); // Devuelve el costo
        } else {
            return ResponseEntity.notFound().build(); // Devuelve un 404 si no se encuentra el vehículo
        }
    }

    @Getter
    @Setter
    private static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message; // Constructor para inicializar el mensaje
        }
    }
}