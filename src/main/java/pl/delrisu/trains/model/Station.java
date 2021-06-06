package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
public class Station {

    @Id
    private String stationCode;
    private String fullName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private List<Silo> silos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private List<Train> trains;
}
