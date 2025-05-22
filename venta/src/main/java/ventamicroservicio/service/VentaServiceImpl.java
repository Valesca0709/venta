package ventamicroservicio.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ventamicroservicio.Model.ProductoVenta;
import ventamicroservicio.Model.Venta;
import ventamicroservicio.dto.ProductoDTO;
import ventamicroservicio.repository.VentaRepository;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String PRODUCTO_URL = "http://localhost:8087/api/v1/productos/";

    @Override
    public Venta crearVenta(Venta venta) {
    double totalBase = 0;

    for (ProductoVenta productoVenta : venta.getProductos()) {
        productoVenta.setVenta(venta); // establecer relación bidireccional

        // Obtener el ID del producto que se está vendiendo
        int idProducto = productoVenta.getIdProducto();

        // Construir la URL para consultar el microservicio de productos
        String urlProducto = PRODUCTO_URL + idProducto;

        try {
            // Llamada al microservicio de producto
            ProductoDTO producto = restTemplate.getForObject(urlProducto, ProductoDTO.class);

            if (producto == null) {
                throw new RuntimeException("Producto con ID " + idProducto + " no encontrado.");
            }

            // Validar stock
            if (producto.getStock() < productoVenta.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto ID " + idProducto);
            }

            // Usar el precio real del producto desde el microservicio
            double precio = producto.getPrecioUnitario();
            productoVenta.setPrecioUnitario(precio);

            // Calcular total del producto
            double totalProducto = precio * productoVenta.getCantidad();
            productoVenta.setTotalProducto(totalProducto);

            // Acumular al total base
            totalBase += totalProducto;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el producto ID " + idProducto + ": " + e.getMessage());
        }
    }

    // Asignar el precio base acumulado
    venta.setPrecioBase(totalBase);

    // Calcular descuento (si existe)
    double descuento = (venta.getDescuento() != null) ? totalBase * venta.getDescuento() : 0;
    double neto = totalBase - descuento;

    // Calcular IVA (si existe)
    double iva = (venta.getIvaPorcentaje() != null) ? neto * (venta.getIvaPorcentaje() / 100.0) : 0;

    // Calcular total final
    venta.setTotalVenta(neto + iva);

    // Guardar la venta con todos los productos
    return ventaRepository.save(venta);
}


    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public void eliminarVenta(Integer idVenta) {
    if (!ventaRepository.existsById(idVenta)) {
        throw new RuntimeException("Venta no encontrada con ID: " + idVenta);
    }
        ventaRepository.deleteById(idVenta);
    }

    @Override
    public Venta actualizarVenta(Integer id, Venta ventaActualizada) {
         Venta ventaExistente = ventaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

    // Campos que puedes actualizar
    ventaExistente.setFechaVenta(ventaActualizada.getFechaVenta());
    ventaExistente.setDescuento(ventaActualizada.getDescuento());
    ventaExistente.setIvaPorcentaje(ventaActualizada.getIvaPorcentaje());
    ventaExistente.setTotalVenta(ventaActualizada.getTotalVenta());

     return ventaRepository.save(ventaExistente);
    }

    @Override
    public Venta obtenerVentaPorId(Integer id) {
    return ventaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
   }
}