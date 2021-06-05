package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Train {

    @Id
    private String trainCode;
    private String fullName;
    @ManyToOne(cascade = CascadeType.ALL)
    private Type type;
    private BigDecimal load;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stationCode")
    private Station station;
}
