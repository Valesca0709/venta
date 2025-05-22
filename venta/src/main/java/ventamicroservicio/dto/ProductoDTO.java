package ventamicroservicio.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private int idProducto;
    private String nombre;
    private String marca;
    private String descripcion;
    private int stock;
    private double precioUnitario;
}

