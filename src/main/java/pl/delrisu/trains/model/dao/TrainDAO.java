package pl.delrisu.trains.model.dao;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Data
@Entity
public class TrainDAO {

    @Id
    private String code;
    private String fullName;
    @ManyToOne
    private TypeDAO type;
    private BigDecimal load;
    @ManyToOne
    private StationDAO station;
}
