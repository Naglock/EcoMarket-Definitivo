package cl.ecomarket.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductoInventariado {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer stock;
    @ManyToOne // Relacion de muchos a uno con producto
    @JoinColumn(name = "producto_id")
    private Producto producto; // ID del producto

}
