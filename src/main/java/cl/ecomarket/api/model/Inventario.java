package cl.ecomarket.api.model;


import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer stock;
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
    @ManyToOne
    @JoinColumn(name = "tienda_id")
    private Tienda tienda;

}
