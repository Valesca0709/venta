package ventamicroservicio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ventamicroservicio.Model.Venta;
import ventamicroservicio.service.VentaService;


@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@RequestBody Venta venta) {
        Venta nuevaVenta = ventaService.crearVenta(venta);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return new ResponseEntity<>(ventaService.listarVentas(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Integer id) {
    ventaService.eliminarVenta(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204: eliminado sin contenido
  }

  @GetMapping("/{id}")
  public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Integer id) {
    Venta venta = ventaService.obtenerVentaPorId(id);
    return new ResponseEntity<>(venta, HttpStatus.OK);
   }

   @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizarVenta(@PathVariable Integer id, @RequestBody Venta venta) {
        Venta actualizada = ventaService.actualizarVenta(id, venta);
        return new ResponseEntity<>(actualizada, HttpStatus.OK);
    }
}

