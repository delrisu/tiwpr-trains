package pl.delrisu.trains.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.delrisu.trains.mapper.CustomMapper;
import pl.delrisu.trains.model.DTO.TrainDTO;
import pl.delrisu.trains.model.DTO.TransshipmentDTO;
import pl.delrisu.trains.model.POST.SiloPOST;
import pl.delrisu.trains.model.POST.StationPOST;
import pl.delrisu.trains.model.*;
import pl.delrisu.trains.model.PUT.TrainPUT;
import pl.delrisu.trains.repository.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
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

    @Autowired
    CustomMapper customMapper;

    @Transactional
    public Transshipment transship(TransshipmentDTO transshipmentDTO) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(transshipmentDTO.getTrainCode());
        Optional<Station> optionalStation = stationRepository.findByStationCode(transshipmentDTO.getStationCode());


        if (optionalTrain.isPresent() && optionalStation.isPresent()) {
            Transshipment transshipment = customMapper.mapTransshipmentDTOToTransshipment(transshipmentDTO);
            transshipment.setDate(LocalDateTime.now());
            List<Silo> silos = siloRepository.findAllByStationAndType(optionalStation.get(), optionalTrain.get().getType());
            Train train = optionalTrain.get();
            Station station = optionalStation.get();

            if (silos.size() != 0) {
                train.setStation(station);
                switch (transshipmentDTO.getDirection()) {
                    case STATION_TO_TRAIN:
                        transshipment.setLoad(silos.get(0).getLoad());
                        train.setLoad(train.getLoad().add(silos.get(0).getLoad()));
                        silos.get(0).setLoad(BigDecimal.valueOf(0));
                        break;
                    case TRAIN_TO_STATION:
                        transshipment.setLoad(train.getLoad());
                        silos.get(0).setLoad(train.getLoad().add(silos.get(0).getLoad()));
                        train.setLoad(BigDecimal.valueOf(0));
                        break;
                }
                siloRepository.save(silos.get(0));
                trainRepository.save(train);
                transshipmentRepository.save(transshipment);
            }

            return transshipment;
        } else {
            return null;
        }
    }

    @Transactional
    public Train prepareTrain(TrainDTO trainDTO) {

        Train train = new Train();
        train.setTrainCode(trainDTO.getTrainCode());
        train.setFullName(trainDTO.getFullName());
        train.setLoad(trainDTO.getLoad());

        Optional<Type> optionalType = typeRepository.findByTypeCode(trainDTO.getTypeCode());
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
    public Train prepareTrain(TrainPUT trainPUT, String trainCode) {

        Train train = new Train();
        train.setTrainCode(trainCode);
        train.setFullName(trainPUT.getFullName());
        train.setLoad(trainPUT.getLoad());

        Optional<Type> optionalType = typeRepository.findByTypeCode(trainPUT.getTypeCode());
        optionalType.ifPresent(train::setType);
        Optional<Station> optionalStation = stationRepository.findByStationCode(trainPUT.getStationCode());
        optionalStation.ifPresent(train::setStation);
        if (train.getType() != null && train.getStation() != null) {
            return train;
        } else {
            return null;
        }
    }

    @Transactional
    public Silo prepareSilo(String stationCode, SiloPOST siloPOST) {

        Silo silo = new Silo();
        silo.setLoad(siloPOST.getLoad());

        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);
        Optional<Type> optionalType = typeRepository.findByTypeCode(siloPOST.getTypeCode());

        if (optionalType.isPresent() && optionalStation.isPresent()) {
            silo.setType(optionalType.get());
            Station station = optionalStation.get();
            silo.setStation(station);
            station.getSilos().add(silo);
            return silo;
        }

        return null;
    }

    public Station prepareStation(StationPOST stationPOST) {

        Station station = new Station();

        station.setStationCode(stationPOST.getStationCode());
        station.setFullName(stationPOST.getFullName());
        station.setSilos(new ArrayList<>());
        station.setTrains(new ArrayList<>());

        return station;
    }
}
