package ventamicroservicio.dto;

import lombok.Data;

@Data
public class SucursalDTO {
    private int idSucursal;
    private String nombre;
    private String direccion;
    private String telefono;
    private String ciudad;
    private String region;
}
