package pl.delrisu.trains.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Train;
import pl.delrisu.trains.model.Type;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, String> {

    List<Train> findAll();

    Page<Train> findAll(Pageable pageable);

    List<Train> findAllByStation(Station station);

    List<Train> findAllByType(Type type);

    Optional<Train> findByTrainCode(String code);
}
