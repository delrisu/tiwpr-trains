package pl.delrisu.trains.model.POST;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StationPOST {
    @NotBlank(message = "Station code must be provided")
    private String stationCode;
    @NotBlank(message = "Full name must be provided")
    private String fullName;
}
