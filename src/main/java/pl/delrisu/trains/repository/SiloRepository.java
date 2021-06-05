package pl.delrisu.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.delrisu.trains.model.Silo;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Type;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiloRepository extends JpaRepository<Silo, Long> {

    List<Silo> findAllByStation(Station station);

    List<Silo> findAllByStationAndType(Station station, Type type);

    Optional<Silo> getById(Long id);

}
