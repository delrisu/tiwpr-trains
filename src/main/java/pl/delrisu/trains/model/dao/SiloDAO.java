package pl.delrisu.trains.model.dao;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class SiloDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal load;
    @ManyToOne
    private TypeDAO type;
}
