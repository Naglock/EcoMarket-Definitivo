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
    Faker faker = new Faker();
    Random random = new Random();

}
