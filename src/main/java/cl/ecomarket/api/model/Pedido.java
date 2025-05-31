package cl.ecomarket.api.model;

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
    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Usuario cliente;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="detalle_id")
    private DetallePedido detalle;

}
