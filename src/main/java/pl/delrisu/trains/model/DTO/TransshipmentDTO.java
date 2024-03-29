package pl.delrisu.trains.model.DTO;

import lombok.Data;
import pl.delrisu.trains.model.Transshipment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransshipmentDTO {
    @NotNull
    private Transshipment.Direction direction;
    @NotBlank(message = "Train code must be provided")
    private String trainCode;
    @NotBlank(message = "Station code must be provided")
    private String stationCode;

}
