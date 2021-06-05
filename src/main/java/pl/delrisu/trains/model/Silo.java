package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Silo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal load;
    @ManyToOne(cascade = CascadeType.ALL)
    private Type type;
    @ManyToOne(cascade = CascadeType.ALL)
    private Station station;
}
