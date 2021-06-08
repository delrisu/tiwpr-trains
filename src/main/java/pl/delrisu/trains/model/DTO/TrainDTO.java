package pl.delrisu.trains.model.DTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TrainDTO {
    @NotBlank(message = "Train code must be provided")
    private String trainCode;
    @NotBlank(message = "Full name must be provided")
    private String fullName;
    @NotBlank(message = "Type code must be provided")
    private String typeCode;
    @NotBlank(message = "Station code must be provided")
    private String stationCode;
    @NotNull(message = "Load must be provided")
    private BigDecimal load;
}
