package cl.ecomarket.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tienda")
@Data
public class Tienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;

}

   