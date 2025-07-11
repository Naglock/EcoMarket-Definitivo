package cl.ecomarket.api.model;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "producto_inventariado_id")
    private List<ProductoInventariado> productosInventariados = new ArrayList<>();
    @OneToOne // Relacion uno a uno con Tienda (corregido).
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

}
