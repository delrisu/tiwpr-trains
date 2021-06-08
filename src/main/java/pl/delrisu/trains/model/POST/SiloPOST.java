package pl.delrisu.trains.model.POST;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SiloPOST {
    private Long id;
    @NotNull(message = "Load must be provided")
    private BigDecimal load;
    @NotBlank(message = "Type code must be provided")
    private String typeCode;
}
