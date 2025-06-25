package cl.ecomarket.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double total;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    private Estados estado;
    @OneToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}