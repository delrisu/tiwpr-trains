package pl.delrisu.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.delrisu.trains.model.Transshipment;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransshipmentRepository extends JpaRepository<Transshipment, UUID> {

    Optional<Transshipment> findByUuid(UUID uuid);

}
