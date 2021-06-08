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
import pl.delrisu.trains.model.DTO.TransshipmentDTO;
import pl.delrisu.trains.model.POST.StationPOST;
import pl.delrisu.trains.model.*;
import pl.delrisu.trains.repository.*;
import pl.delrisu.trains.service.ShipmentService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

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

    @GetMapping("/trains/{trainCode}")
    public ResponseEntity<?> getTrainByCode(@PathVariable String trainCode) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);
        if (optionalTrain.isPresent()) {
            return ResponseEntity.ok(customMapper.mapTrainToTrainDTO(optionalTrain.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
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

    @PostMapping("/trains/{trainCode}/load")
    public ResponseEntity<?> addLoadToTrain(@PathVariable String trainCode, @RequestBody BigDecimal load) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);
        if (optionalTrain.isPresent()) {
            Train train = optionalTrain.get();
            train.setLoad(train.getLoad().add(load));
            trainRepository.save(train);
            return ResponseEntity.ok(customMapper.mapTrainToTrainDTO(train));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stations")
    public List<StationDTO> getAllStations() {
        List<StationDTO> stationDTOs = new ArrayList<>();
        stationRepository.findAll().forEach(station -> stationDTOs.add(customMapper.mapStationToStationDTO(station)));
        return stationDTOs;
    }

    @PostMapping("/stations")
    public ResponseEntity<?> postStation(@Valid @RequestBody StationPOST stationPOST) {
        Station station = shipmentService.prepareStation(stationPOST);
        stationRepository.save(station);
        return ResponseEntity.ok(customMapper.mapStationToStationDTO(station));
    }

    @GetMapping("/station/{stationCode}/silo")
    public ResponseEntity<?> getAllSilos(@PathVariable String stationCode) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);

        if (optionalStation.isPresent()) {
            List<SiloDTO> siloDTOs = new ArrayList<>();
            optionalStation.get().getSilos().forEach(silo -> siloDTOs.add(customMapper.mapSiloToSiloDTO(silo)));
            return ResponseEntity.ok(siloDTOs);
        } else {
            return ResponseEntity.badRequest().body("No station with that code");
        }
    }

    @GetMapping("/station/{stationCode}/silo/{siloId}")
    public ResponseEntity<?> getSiloByStationAndSiloId(@PathVariable Long siloId, @PathVariable String stationCode) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);

        if (optionalStation.isPresent()) {
            Optional<Silo> optionalSilo = siloRepository.findByIdAndStation(siloId, optionalStation.get());
            if (optionalSilo.isPresent()) {
                return ResponseEntity.ok(customMapper.mapSiloToSiloDTO(optionalSilo.get()));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/station/{stationCode}/silo")
    public ResponseEntity<?> postSilo(@PathVariable String stationCode, @Valid @RequestBody SiloDTO silo) {
        Silo siloRet = shipmentService.prepareSilo(stationCode, silo);

        if (siloRet != null) {
            return ResponseEntity.ok(customMapper.mapSiloToSiloDTO(siloRet));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/types")
    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    @GetMapping("/types/{typeCode}")
    public ResponseEntity<?> getTypeByCode(@PathVariable String typeCode) {
        Optional<Type> optionalType = typeRepository.findByTypeCode(typeCode);

        if (optionalType.isPresent()) {
            return ResponseEntity.ok(optionalType.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/types")
    public ResponseEntity<?> postType(@Valid @RequestBody Type type) {
        return ResponseEntity.ok(typeRepository.save(type));
    }


    @GetMapping("/transshipments")
    public List<Transshipment> getAllTransshipments() {
        return transshipmentRepository.findAll();
    }


    @PostMapping("/transshipments")
    public ResponseEntity<?> postTransshipment(@Valid @RequestBody TransshipmentDTO transshipmentDTO) {
        Transshipment transshipment = shipmentService.transship(transshipmentDTO);
        if (transshipment != null) {
            return ResponseEntity.ok(transshipment);
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
