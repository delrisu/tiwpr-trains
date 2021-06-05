package pl.delrisu.trains.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Transshipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;
    @ManyToOne(cascade = CascadeType.ALL)
    private Train train;
    @ManyToOne(cascade = CascadeType.ALL)
    private Station station;
    private Direction direction;
    private LocalDateTime date;

    public enum Direction {
        TRAIN_TO_STATION, STATION_TO_TRAIN
    }
}
