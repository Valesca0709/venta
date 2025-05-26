package ventamicroservicio.dto;

import lombok.Data;

@Data
public class ProductoSucursalDTO {
    private int idProductoSucursal;
    private int idSucursal;
    private int idProducto; 
    private int cantidad;
    private double precio_unitario;
}
