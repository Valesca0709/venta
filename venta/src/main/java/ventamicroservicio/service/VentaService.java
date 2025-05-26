package ventamicroservicio.service;


import java.util.List;

import ventamicroservicio.Model.ProductoSolicitud;
import ventamicroservicio.Model.Venta;


public interface VentaService {
    List<Venta> listarVentas();
    void eliminarVenta(Integer idVenta);
    Venta actualizarVenta (Integer id, Venta ventaActualizada);
    Venta obtenerVentaPorId(Integer id);
    Venta obtenerSucursalPorId(int idSucursal, int idProducto);
    Venta crearVenta(int idSucursal, List<ProductoSolicitud> productosSolicitados);
}