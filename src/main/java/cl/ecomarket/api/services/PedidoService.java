package cl.ecomarket.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Pedido;
import cl.ecomarket.api.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> obtenerPorClienteId(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido encontrarPorId(Long id) {
        return pedidoRepository.findById(id).get();
    }

    public boolean existePorId(Long id) {
        return pedidoRepository.existsById(id);
    }
}
