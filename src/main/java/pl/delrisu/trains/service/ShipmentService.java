package pl.delrisu.trains.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.delrisu.trains.model.DTO.TrainDTO;
import pl.delrisu.trains.model.DTO.TransshipmentDTO;
import pl.delrisu.trains.model.Silo;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Train;
import pl.delrisu.trains.model.Type;
import pl.delrisu.trains.repository.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    @Autowired
    SiloRepository siloRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    TrainRepository trainRepository;
    @Autowired
    TransshipmentRepository transshipmentRepository;
    @Autowired
    TypeRepository typeRepository;


    public void transship(TransshipmentDTO transshipmentDTO) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(transshipmentDTO.getTrainCode());
        Optional<Station> optionalStation = stationRepository.findByStationCode(transshipmentDTO.getStationCode());


        if (optionalTrain.isPresent() && optionalStation.isPresent()) {
            List<Silo> silos = siloRepository.findAllByStationAndType(optionalStation.get(), optionalTrain.get().getType());
            Train train = optionalTrain.get();
            Station station = optionalStation.get();

            if (train.getStation().getStationCode().equals(station.getStationCode())) {
                if (silos.size() != 0) {
                    switch (transshipmentDTO.getDirection()) {
                        case STATION_TO_TRAIN:
                            train.setLoad(train.getLoad().add(silos.get(0).getLoad()));
                            silos.get(0).setLoad(BigDecimal.valueOf(0));
                            break;
                        case TRAIN_TO_STATION:
                            silos.get(0).setLoad(train.getLoad().add(silos.get(0).getLoad()));
                            train.setLoad(BigDecimal.valueOf(0));
                            break;
                    }
                    siloRepository.save(silos.get(0));
                    trainRepository.save(train);
                }
            }
        }
    }

    public Train prepareTrain(TrainDTO trainDTO) {

        Train train = new Train();
        train.setTrainCode(trainDTO.getCode());
        train.setFullName(trainDTO.getFullName());

        Optional<Type> optionalType = typeRepository.findByCode(trainDTO.getTypeCode());
        optionalType.ifPresent(train::setType);
        Optional<Station> optionalStation = stationRepository.findByStationCode(trainDTO.getStationCode());
        optionalStation.ifPresent(train::setStation);
        if (train.getType() != null && train.getStation() != null) {
            return train;
        } else {
            return null;
        }
    }

    @Transactional
    public Silo prepareSilo(String stationCode, Silo silo) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);
        Optional<Type> optionalType = typeRepository.findByCode(silo.getType().getCode());

        if (optionalStation.isPresent()) {
            Station station = optionalStation.get();
            silo.setStation(station);
            station.getSilos().add(silo);
            stationRepository.save(station);
            return silo;
        }

        return null;
    }
}
