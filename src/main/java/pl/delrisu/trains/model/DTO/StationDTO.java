package pl.delrisu.trains.model.DTO;

import lombok.Data;

import java.util.List;

@Data
public class StationDTO {
    private String stationCode;
    private String fullName;
    private List<SiloDTO> silos;
    private List<TrainDTO> trains;
}
