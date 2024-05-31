package qlt.carparking.Repository;
/*El repositorio extiende JpaRepository, proporcionando métodos CRUD para tu entidad, así como la capacidad de
definir otros métodos de consulta personalizados.*/
import org.springframework.data.jpa.repository.JpaRepository;
import qlt.carparking.Model.Vehicule;
// El repositorio extiende JpaRepository, proporcionando métodos CRUD para la entidad Vehicule
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {
    Vehicule findByPlate(String plate);
    // Spring Data JPA utiliza este repositorio para realizar operaciones CRUD
}

