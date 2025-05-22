package ventamicroservicio.service;


import java.util.List;
import ventamicroservicio.Model.Venta;


public interface VentaService {
    Venta crearVenta(Venta venta);
    List<Venta> listarVentas();
    void eliminarVenta(Integer idVenta);
    Venta actualizarVenta (Integer id, Venta ventaActualizada);
    Venta obtenerVentaPorId(Integer id);
}