package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Transshipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;
    private String trainCode;
    private String stationCode;
    private Direction direction;
    private LocalDateTime date;
    private BigDecimal load;

    public enum Direction {
        TRAIN_TO_STATION, STATION_TO_TRAIN
    }
}
