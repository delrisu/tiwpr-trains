package pl.delrisu.trains.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.delrisu.trains.model.Type;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, String> {

    List<Type> findAll();

    Optional<Type> findByTypeCode(String code);

}
