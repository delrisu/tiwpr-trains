package pl.delrisu.trains.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.delrisu.trains.model.Transshipment;

@Data
public class TransshipmentDTO {

    @JsonProperty(required = true)
    private Transshipment.Direction direction;
    @JsonProperty(required = true)
    private String trainCode;
    @JsonProperty(required = true)
    private String stationCode;

}
