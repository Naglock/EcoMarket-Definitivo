package cl.ecomarket.api.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    private Long id;
    private int cantidad;
    //@ManyToOne(name="producto_id")
    //private Producto producto;

}
