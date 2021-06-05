package pl.delrisu.trains.model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TrainDTO {
    @NotBlank(message = "Code must be provided")
    private String code;
    @NotBlank(message = "Full name must be provided")
    private String fullName;
    @NotBlank(message = "Type code must be provided")
    private String typeCode;
    @NotBlank(message = "Station code must be provided")
    private String stationCode;
}
