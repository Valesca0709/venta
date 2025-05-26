package ventamicroservicio.request;

import java.util.List;
import ventamicroservicio.Model.ProductoSolicitud;

public class VentaRequest {
    private int idSucursal;
    private List<ProductoSolicitud> productosSolicitados;
    

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public List<ProductoSolicitud> getProductosSolicitados() {
        return productosSolicitados;
    }

    public void setProductosSolicitados(List<ProductoSolicitud> productosSolicitados) {
        this.productosSolicitados = productosSolicitados;
    }
}
