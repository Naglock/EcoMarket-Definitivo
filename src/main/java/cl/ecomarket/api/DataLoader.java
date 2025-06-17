package cl.ecomarket.api;

import cl.ecomarket.api.model.*;
import cl.ecomarket.api.repository.*;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner{

    @Autowired
    private ProductoRepository productoRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception{
             Faker faker = new Faker();
             Random random = new Random();

            for (int i = 0; i < 10; i++) {
            Producto producto = new Producto();
            producto.setNombre(faker.commerce().productName());
            producto.setDescripcion(faker.lorem().sentence());
            producto.setPrecio(Double.parseDouble(faker.commerce().price(1000.0, 100000.0)));
            productoRepository.save(producto);
    }
    System.out.println("Datos de prueba cargados exitosamente.");
    }
    


}
