package pl.delrisu.trains.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiloDTO {
    private Long id;
    @NotNull(message = "Load must be provided")
    private BigDecimal load;
    @NotBlank(message = "Type code must be provided")
    private String typeCode;
    @NotBlank(message = "Station code must be provided")
    private String stationCode;
}
