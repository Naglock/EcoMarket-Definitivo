package cl.ecomarket.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.repository.TiendaRepository;

@Service
public class TiendaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    public List<Tienda> obtenerTodasLasTiendas() {
        return tiendaRepository.findAll();
    }

    public Optional<Tienda> obtenerTiendaPorId(Long id) {
        return tiendaRepository.findById(id);
    }

    public Tienda guardarTienda(Tienda tienda) {
        return tiendaRepository.save(tienda);
    }

    public void eliminarTienda(Long id) {
        tiendaRepository.deleteById(id);
    }

    public List<Tienda> buscarPorDiaSemana(String diaSemana) {
        return tiendaRepository.findAll().stream()
                .filter(tienda -> tienda.getDiaSemana().equalsIgnoreCase(diaSemana))
                .toList();
    }
}