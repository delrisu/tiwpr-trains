package pl.delrisu.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.delrisu.trains.model.dao.TrainDAO;

public interface StationRepository extends JpaRepository<TrainDAO, Long> {
}
