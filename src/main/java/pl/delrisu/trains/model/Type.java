package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@Entity
public class Type {

    @Id
    @NotBlank(message = "Type code must be provided")
    private String typeCode;
    @NotBlank(message = "Full name must be provided")
    private String fullName;
}
