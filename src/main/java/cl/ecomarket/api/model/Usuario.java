package cl.ecomarket.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    // Donde estan los @Column es para indicar que esos datos si o si deban estar
    // para la creacion del usuario posteriormente, los demas se pueden settear despues
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String pnombre; //primer nombre
    private String snombre; //segundo nombre
    @Column(nullable = false)
    private String appaterno; //apellido paterno
    private String apmaterno; //apellido materno
    private String correo;
    @Column(nullable = false)
    private String direccion; 

    

}
