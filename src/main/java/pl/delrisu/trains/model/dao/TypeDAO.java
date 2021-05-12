package pl.delrisu.trains.model.dao;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class TypeDAO {

    @Id
    private String code;
    private String fullName;
}
