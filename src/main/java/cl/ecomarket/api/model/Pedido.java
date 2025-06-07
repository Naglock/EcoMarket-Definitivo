package cl.ecomarket.api.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Estados estado;
    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Usuario cliente;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedido> items;
    @OneToOne // Relacion uno a uno con Tienda (corregido).
    @JoinColumn(name="tienda_id")
    private Tienda tienda;

}
