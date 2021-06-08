package pl.delrisu.trains.mapper;

import org.springframework.stereotype.Service;
import pl.delrisu.trains.model.DTO.SiloDTO;
import pl.delrisu.trains.model.DTO.StationDTO;
import pl.delrisu.trains.model.DTO.TrainDTO;
import pl.delrisu.trains.model.DTO.TransshipmentDTO;
import pl.delrisu.trains.model.Silo;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Train;
import pl.delrisu.trains.model.Transshipment;

import java.util.ArrayList;

@Service
public class CustomMapper {

    public StationDTO mapStationToStationDTO(Station station) {
        StationDTO stationDTO = new StationDTO();
        stationDTO.setStationCode(station.getStationCode());
        stationDTO.setFullName(station.getFullName());
        stationDTO.setSilos(new ArrayList<>());
        stationDTO.setTrains(new ArrayList<>());

        station.getSilos().forEach(silo -> stationDTO.getSilos().add(mapSiloToSiloDTO(silo)));
        station.getTrains().forEach(train -> stationDTO.getTrains().add(mapTrainToTrainDTO(train)));

        return stationDTO;
    }

    public SiloDTO mapSiloToSiloDTO(Silo silo) {
        SiloDTO siloDTO = new SiloDTO();
        siloDTO.setId(silo.getId());
        siloDTO.setLoad(silo.getLoad());
        siloDTO.setTypeCode(silo.getType().getTypeCode());
        siloDTO.setStationCode(silo.getStation().getStationCode());

        return siloDTO;
    }

    public TrainDTO mapTrainToTrainDTO(Train train) {
        TrainDTO trainDTO = new TrainDTO();
        trainDTO.setTrainCode(train.getTrainCode());
        trainDTO.setFullName(train.getFullName());
        trainDTO.setTypeCode(train.getType().getTypeCode());
        trainDTO.setStationCode(train.getStation().getStationCode());
        trainDTO.setLoad(train.getLoad());

        return trainDTO;
    }

    public TransshipmentDTO mapTransshipmentToTransshipmentDTO(Transshipment transshipment) {
        TransshipmentDTO transshipmentDTO = new TransshipmentDTO();
        transshipmentDTO.setDirection(transshipment.getDirection());
        transshipmentDTO.setTrainCode(transshipment.getTrainCode());
        transshipmentDTO.setStationCode(transshipmentDTO.getStationCode());

        return transshipmentDTO;
    }

    public Transshipment mapTransshipmentDTOToTransshipment(TransshipmentDTO transshipmentDTO) {
        Transshipment transshipment = new Transshipment();
        transshipment.setDirection(transshipmentDTO.getDirection());
        transshipment.setTrainCode(transshipmentDTO.getTrainCode());
        transshipment.setStationCode(transshipmentDTO.getStationCode());

        return transshipment;
    }
}
