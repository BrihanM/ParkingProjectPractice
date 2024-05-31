package qlt.carparking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import qlt.carparking.Controller.ParkingController;
import qlt.carparking.Model.Vehicule;
import qlt.carparking.Repository.VehiculeRepository;
import qlt.carparking.Service.ParkingService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingController.class)
public class ParkingControllerEditVehiculeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingService parkingService;

    @MockBean
    private VehiculeRepository vehiculeRepository;

    private Vehicule existingVehicule;

    @BeforeEach
    public void setUp() {
        existingVehicule = new Vehicule();
        existingVehicule.setId(1L);
        existingVehicule.setPlate("ABC123");
        existingVehicule.setEntryTime(LocalTime.from(LocalDateTime.now()));
        existingVehicule.setWash(false);
        existingVehicule.setHelmet(false);
        existingVehicule.setType("carro");

        Mockito.when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(existingVehicule));
    }

    @Test
    public void testEditVehiculeType() throws Exception {
        Vehicule updatedVehicule = new Vehicule();
        updatedVehicule.setId(1L);
        updatedVehicule.setPlate("ABC123");
        updatedVehicule.setEntryTime(LocalTime.from(LocalDateTime.now()));
        updatedVehicule.setWash(false);
        updatedVehicule.setHelmet(false);
        updatedVehicule.setType("moto");

        mockMvc.perform(put("/parking/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"ABC123\",\"type\":\"moto\"}"))
                .andExpect(status().is3xxRedirection());

        Mockito.verify(vehiculeRepository).save(Mockito.argThat(vehicule -> vehicule.getType().equals("moto")));
    }

    @Test
    public void testEditVehiculeTypeInvalidId() throws Exception {
        mockMvc.perform(put("/parking/edit/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"ABC123\",\"type\":\"moto\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEditVehiculeTypeNoChange() throws Exception {
        mockMvc.perform(put("/parking/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"ABC123\",\"type\":\"carro\"}"))
                .andExpect(status().is3xxRedirection());

        Mockito.verify(vehiculeRepository).save(Mockito.argThat(vehicule -> vehicule.getType().equals("carro")));
    }

    @Test
    public void testEditVehiculeTypeWithHelmet() throws Exception {
        Vehicule updatedVehicule = new Vehicule();
        updatedVehicule.setId(1L);
        updatedVehicule.setPlate("ABC123");
        updatedVehicule.setEntryTime(LocalTime.from(LocalDateTime.now()));
        updatedVehicule.setWash(false);
        updatedVehicule.setHelmet(true);
        updatedVehicule.setType("moto");

        mockMvc.perform(put("/parking/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"ABC123\",\"type\":\"moto\",\"helmet\":true}"))
                .andExpect(status().is3xxRedirection());

        Mockito.verify(vehiculeRepository).save(Mockito.argThat(vehicule -> vehicule.getType().equals("moto") && vehicule.isHelmet()));
    }

    @Test
    public void testEditVehiculeTypeWithWash() throws Exception {
        Vehicule updatedVehicule = new Vehicule();
        updatedVehicule.setId(1L);
        updatedVehicule.setPlate("ABC123");
        updatedVehicule.setEntryTime(LocalTime.from(LocalDateTime.now()));
        updatedVehicule.setWash(true);
        updatedVehicule.setHelmet(false);
        updatedVehicule.setType("moto");

        mockMvc.perform(put("/parking/edit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"ABC123\",\"type\":\"moto\",\"wash\":true}"))
                .andExpect(status().is3xxRedirection());

        Mockito.verify(vehiculeRepository).save(Mockito.argThat(vehicule -> vehicule.getType().equals("moto") && vehicule.isWash()));
    }
}