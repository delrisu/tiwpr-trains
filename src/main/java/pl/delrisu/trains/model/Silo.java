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
    @ManyToOne
    private Type type;
    @ManyToOne
    @JoinColumn(name = "stationCode")
    private Station station;
}
