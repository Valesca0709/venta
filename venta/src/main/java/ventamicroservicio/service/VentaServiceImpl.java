package ventamicroservicio.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ventamicroservicio.Model.ProductoSolicitud;
import ventamicroservicio.Model.ProductoVenta;
import ventamicroservicio.Model.Venta;
import ventamicroservicio.dto.ProductoSucursalDTO;
import ventamicroservicio.repository.VentaRepository;

@Service
public class VentaServiceImpl implements VentaService {
    


    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String SUCURSAL_URL = "http://localhost:8067/api/v1/productoSucursal/";
   

 @Override
 public Venta crearVenta(int idSucursal, List<ProductoSolicitud> productosSolicitados) {
    double totalBase = 0;
    List<ProductoVenta> productosVenta = new ArrayList<>();

    for (ProductoSolicitud solicitud : productosSolicitados) {
        int idProducto = solicitud.getIdProducto();
        int cantidadSolicitada = solicitud.getCantidad();

        // Construir la URL con query params
        String url = SUCURSAL_URL + "buscar?idProducto=" + idProducto + "&idSucursal=" + idSucursal;

        // Llamada al microservicio → obtener array
        ProductoSucursalDTO[] productosArray = restTemplate.getForObject(url, ProductoSucursalDTO[].class);

        if (productosArray == null || productosArray.length == 0) {
            throw new RuntimeException("Producto ID " + idProducto + " no encontrado en sucursal ID " + idSucursal);
        }

        // Usar el primer resultado (ajusta si necesitas usar varios)
        ProductoSucursalDTO productoSucursal = productosArray[0];

        // Validar stock
        if (productoSucursal.getCantidad() < cantidadSolicitada) {
            throw new RuntimeException("Stock insuficiente para el producto ID " + idProducto + " en sucursal ID " + idSucursal);
        }

        // Calcular precios
        double precioUnitario = productoSucursal.getPrecio_unitario();
        double totalProducto = precioUnitario * cantidadSolicitada;

        // Crear detalle de producto venta
        ProductoVenta productoVenta = new ProductoVenta();
        productoVenta.setIdProducto(idProducto);
        productoVenta.setCantidad(cantidadSolicitada);
        productoVenta.setPrecioUnitario(precioUnitario);
        productoVenta.setTotalProducto(totalProducto);

        productosVenta.add(productoVenta);
        totalBase += totalProducto;
    }

    // Crear objeto venta final
    Venta venta = new Venta();
    venta.setIdSucursal(idSucursal);
    venta.setProductos(productosVenta);
    venta.setPrecioBase(totalBase);

    // Si aplica, calcula descuentos e IVA
    double descuento = (venta.getDescuento() != null) ? totalBase * venta.getDescuento() : 0;
    double neto = totalBase - descuento;
    double iva = (venta.getIvaPorcentaje() != null) ? neto * (venta.getIvaPorcentaje() / 100.0) : 0;

    venta.setTotalVenta(neto + iva);

    // Guardar en base de datos
    return ventaRepository.save(venta);
}




    /*@Override
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
}*/


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


    @Override
    public Venta obtenerSucursalPorId(int idSucursal, int idProducto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerSucursalPorId'");
    }

}