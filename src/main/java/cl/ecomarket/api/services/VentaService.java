package cl.ecomarket.api.services;

import cl.ecomarket.api.model.ItemPedido;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.repository.VentaRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class VentaService {
    @Autowired
    private VentaRepository ventaRepository;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public List<Venta> obtenerPorClienteID(Long clienteId) {
        return ventaRepository.findByPedidoClienteId(clienteId);
    }

    public String generarFactura(Long id) {
        Venta venta = obtenerPorId(id);
        if (venta == null) return null;
        StringBuilder factura = new StringBuilder();
        factura.append("======FACTURA ====\n");
        factura.append("==================\n");
        factura.append("N° Venta: ").append(venta.getId()).append("\n");
        factura.append("Cliente ID: ").append(venta.getPedido().getCliente().getId()).append("\n");
        factura.append("Fecha: ").append(venta.getFecha()).append("\n\n");
        factura.append("==================\n");
        factura.append("Detalle:\n");
        for (ItemPedido detalle : venta.getPedido().getItems()) {
            factura.append("- Producto ID: ").append(detalle.getProducto().getId())
                .append(" | Cantidad: ").append(detalle.getCantidad())
                .append(" | Precio Unitario: ").append(detalle.getProducto().getPrecio())
                .append(" | Subtotal: ").append(detalle.getCantidad() * detalle.getProducto().getPrecio()).append("\n");
        }
        factura.append("TOTAL: $").append(venta.getTotal()).append("\n");
        factura.append("================");
        return factura.toString();
    }

    public Venta generarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    public void eliminarVenta(Long id) {
        ventaRepository.deleteById(id);
    }
}
