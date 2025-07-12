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

    public List<Tienda> obtenerTodasLasTiendas(){
        return tiendaRepository.findAll();
    }

    public Tienda obtenerTiendaPorId(Long id) throws Exception {
        if (!tiendaRepository.existsById(id)) {
            throw new Exception("Tienda no encontrada");
        }
        return tiendaRepository.findById(id).get();
    }

    public Tienda guardarTienda(Tienda tienda) throws Exception {
        if (tienda.getId() != null && tiendaRepository.existsById(tienda.getId())) {
            throw new Exception("Ya existe una tienda con ese ID");
        }
        return tiendaRepository.save(tienda);
    }

    public void eliminarTienda(Long id) throws Exception {
        if (!tiendaRepository.existsById(id)) {
            throw new Exception("Tienda no encontrada");
        }
        tiendaRepository.deleteById(id);
    }
}