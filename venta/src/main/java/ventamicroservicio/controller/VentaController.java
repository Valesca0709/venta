package ventamicroservicio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ventamicroservicio.Model.Venta;
import ventamicroservicio.request.VentaRequest;
import ventamicroservicio.service.VentaService;


@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return new ResponseEntity<>(ventaService.listarVentas(), HttpStatus.OK);
    }

    @PostMapping("/crear")
    public ResponseEntity<Venta> crearVenta(@RequestBody VentaRequest request) {
        Venta venta = ventaService.crearVenta(
            request.getIdSucursal(),
            request.getProductosSolicitados()
        );
        return new ResponseEntity<>(venta, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Integer id) {
        Venta venta = ventaService.obtenerVentaPorId(id);
        return new ResponseEntity<>(venta, HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Venta> actualizarVenta(@PathVariable Integer id, @RequestBody Venta venta) {
        Venta actualizada = ventaService.actualizarVenta(id, venta);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer id) {
        ventaService.eliminarVenta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sucursal/{idSucursal}/producto/{idProducto}")
    public ResponseEntity<Venta> obtenerSucursalDeVenta(
            @PathVariable int idSucursal,
            @PathVariable int idProducto) {

        Venta venta = ventaService.obtenerSucursalPorId(idSucursal, idProducto);
        venta.setIdSucursal(idSucursal);

        return ResponseEntity.ok(venta);
    }
}

