package pl.delrisu.trains.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.delrisu.trains.mapper.CustomMapper;
import pl.delrisu.trains.model.DTO.SiloDTO;
import pl.delrisu.trains.model.DTO.StationDTO;
import pl.delrisu.trains.model.DTO.TrainDTO;
import pl.delrisu.trains.model.POST.StationPOST;
import pl.delrisu.trains.model.Silo;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Train;
import pl.delrisu.trains.model.Type;
import pl.delrisu.trains.repository.*;
import pl.delrisu.trains.service.ShipmentService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1")
@RestController
public class ApiController {

    @Autowired
    ShipmentService shipmentService;

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

    @GetMapping("/trains")
    public List<TrainDTO> getAllTrains() {
        List<TrainDTO> trainDTOs = new ArrayList<>();
        trainRepository.findAll().forEach(train -> trainDTOs.add(customMapper.mapTrainToTrainDTO(train)));
        return trainDTOs;
    }

    @GetMapping("/stations")
    public List<StationDTO> getAllStations(){
        List<StationDTO> stationDTOs = new ArrayList<>();
        stationRepository.findAll().forEach(station -> stationDTOs.add(customMapper.mapStationToStationDTO(station)));
        return stationDTOs;
    }

    @PostMapping("/trains")
    public ResponseEntity<?> postTrain(@Valid @RequestBody TrainDTO trainDTO) {
        Train train = shipmentService.prepareTrain(trainDTO);

        if (train != null) {
            trainRepository.save(train);
            return ResponseEntity.ok(trainDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/types")
    public ResponseEntity<?> postType(@RequestBody Type type) {
        return ResponseEntity.ok(typeRepository.save(type));
    }

    @PostMapping("/stations")
    public ResponseEntity<?> postStation(@RequestBody StationPOST stationPOST) {
        Station station = shipmentService.prepareStation(stationPOST);
        stationRepository.save(station);
        return ResponseEntity.ok(customMapper.mapStationToStationDTO(station));
    }

    @PostMapping("/station/{stationCode}/silo")
    public ResponseEntity<?> postSilo(@PathVariable String stationCode, @RequestBody SiloDTO silo) {
        Silo siloRet = shipmentService.prepareSilo(stationCode, silo);

        if (siloRet != null) {
            return ResponseEntity.ok(customMapper.mapSiloToSiloDTO(siloRet));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }


}
