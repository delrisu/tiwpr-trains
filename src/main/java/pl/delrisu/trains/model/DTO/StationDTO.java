package pl.delrisu.trains.model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class StationDTO {
    private String stationCode;
    private String fullName;
    private List<Long> silos;
    private List<String> trains;
}
