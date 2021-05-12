package pl.delrisu.trains.model.dao;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Data
@Entity
public class StationDAO {

    @Id
    private String code;
    private String fullName;
    @OneToMany
    private List<SiloDAO> silos;
    @OneToMany
    private List<TrainDAO> trains;
}
