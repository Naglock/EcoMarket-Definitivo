package cl.ecomarket.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int cantidad;
    @ManyToOne // Relacion muchos a uno con Producto (corregido)
    @JoinColumn(name = "producto_id")
    private Producto producto;

}
