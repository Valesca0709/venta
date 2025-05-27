package ventamicroservicio.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

        Venta venta = new Venta();
        venta.setIdSucursal(idSucursal);
        venta.setFechaVenta(new Date());
        venta.setDescuento(0.0);
        venta.setIvaPorcentaje(19.0);

        for (ProductoSolicitud solicitud : productosSolicitados) {
            int idProducto = solicitud.getIdProducto();
            int cantidadSolicitada = solicitud.getCantidad();

            String url = SUCURSAL_URL + "buscar?idProducto=" + idProducto + "&idSucursal=" + idSucursal;
            ProductoSucursalDTO[] productosArray = restTemplate.getForObject(url, ProductoSucursalDTO[].class);

            if (productosArray == null || productosArray.length == 0) {
                throw new RuntimeException("Producto ID " + idProducto + " no encontrado en sucursal ID " + idSucursal);
            }

            ProductoSucursalDTO productoSucursal = productosArray[0];

            if (productoSucursal.getCantidad() < cantidadSolicitada) {
                throw new RuntimeException("Stock insuficiente para el producto ID " + idProducto + " en sucursal ID " + idSucursal);
            }

            double precioUnitario = productoSucursal.getPrecio_unitario();
            double totalProducto = precioUnitario * cantidadSolicitada;

            ProductoVenta productoVenta = new ProductoVenta();
            productoVenta.setIdProducto(idProducto);
            productoVenta.setCantidad(cantidadSolicitada);
            productoVenta.setPrecioUnitario(precioUnitario);
            productoVenta.setTotalProducto(totalProducto);
            productoVenta.setVenta(venta); // relacion inversa, por que hay el producto vendido a la venta

            productosVenta.add(productoVenta);
            totalBase += totalProducto;
        }

        venta.setProductos(productosVenta);
        venta.setPrecioBase(totalBase);

        double descuento = totalBase * venta.getDescuento(); 
        double neto = totalBase - descuento;
        double iva = neto * (venta.getIvaPorcentaje() / 100.0); 
        venta.setTotalVenta(neto + iva);

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

    @Override
    public Venta obtenerSucursalPorId(int idSucursal, int idProducto) {
        throw new UnsupportedOperationException("Unimplemented method 'obtenerSucursalPorId'");
    }
}