package cl.ecomarket.api.services;

import java.util.List;  
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.repository.TiendaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class TiendaService {

    @Autowired
    private TiendaRepository tiendaRepository;

    public List<Tienda> obtenerTodasLasTiendas() {
        return tiendaRepository.findAll();
    }

    public Tienda obtenerTiendaPorId(Long id) {
        return tiendaRepository.findById(id).get();
    }

    public Tienda guardarTienda(Tienda tienda) {
        return tiendaRepository.save(tienda);
    }

    public void eliminarTienda(Long id) {
        tiendaRepository.deleteById(id);
    }

/*     public List<Tienda> buscarPorDiaSemana(String diaSemana) {
        return tiendaRepository.findAll().stream()
                .filter(tienda -> tienda.getDiaSemana().equalsIgnoreCase(diaSemana))
                .toList();
    } */
}