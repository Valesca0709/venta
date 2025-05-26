package ventamicroservicio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ventamicroservicio.Model.Venta;


@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    // Optional<Venta> findById(Integer id);
}
