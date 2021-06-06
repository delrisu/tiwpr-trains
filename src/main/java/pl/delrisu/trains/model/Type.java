package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Type {

    @Id
    private String typeCode;
    private String fullName;
}
