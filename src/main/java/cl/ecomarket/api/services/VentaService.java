package cl.ecomarket.api.services;

import cl.ecomarket.api.model.Estados;
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
    @Autowired
    private PedidoService pedidoService;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Venta obtenerPorId(Long id) throws Exception{
        if (!ventaRepository.existsById(id)) {
            throw new Exception("Venta no encontrada");
        }   
        return ventaRepository.findById(id).get();
    }

    public List<Venta> obtenerPorClienteID(Long clienteId) throws Exception {
        List<Venta> ventas = ventaRepository.findByPedidoClienteId(clienteId);
        if (ventas == null || ventas.isEmpty()) {
            throw new Exception("No se encontraron ventas para el cliente con ID: " + clienteId);
        }
        return ventas;
    }

    public String generarFactura(Long id) throws Exception {
        if (!ventaRepository.existsById(id)) {
            throw new Exception("Venta no encontrada");
        }
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

    public Venta generarVenta(Venta venta) throws Exception {
        try {
            // Verificar si el pedido existe
            if (venta.getPedido() == null || !pedidoService.existePorId(venta.getPedido().getId())) {
                throw new Exception("El pedido no existe");
            }
            // Establecer el pedido en la venta
            venta.setPedido(pedidoService.encontrarPorId(venta.getPedido().getId()));
        } catch (Exception e) {
            throw new Exception("Error al generar la venta: " + e.getMessage());
        }
        venta.setEstado(Estados.APROBADA); // Establecer el estado de la venta como APROBADA por defecto
        venta.setFecha(java.time.LocalDate.now()); // Establecer la fecha actual de la venta
        // Calcular el total de la venta
        double total = 0.0;
        for (ItemPedido item : venta.getPedido().getItems()) {
            double subtotal = item.getCantidad() * item.getProducto().getPrecio();
            total += subtotal;
        }
        venta.setTotal(total);
        pedidoService.aprobarPedido(venta.getPedido().getId()); // Aprobar el pedido antes de generar la venta
        // Guardar la venta en la base de datos
        return ventaRepository.save(venta);
    }

    public void eliminarVenta(Long id) throws Exception {
        // Verificar si la venta existe
        if (!ventaRepository.existsById(id)) {
            throw new Exception("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }
}
