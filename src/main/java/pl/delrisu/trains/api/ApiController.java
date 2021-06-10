package pl.delrisu.trains.api;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import pl.delrisu.trains.model.POST.SiloPOST;
import pl.delrisu.trains.model.POST.StationPOST;
import pl.delrisu.trains.model.PUT.TrainPUT;
import pl.delrisu.trains.model.*;
import pl.delrisu.trains.repository.*;
import pl.delrisu.trains.service.ShipmentService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
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
    public List<TrainDTO> getAllTrains(@RequestParam(required = false) Optional<Integer> page) {
        List<TrainDTO> trainDTOs = new ArrayList<>();
        if (page.isPresent()) {
            trainRepository.findAll(PageRequest.of(page.get(), 5)).forEach(train -> trainDTOs
                    .add(customMapper.mapTrainToTrainDTO(train)));
        } else {
            trainRepository.findAll().forEach(train -> trainDTOs.add(customMapper.mapTrainToTrainDTO(train)));
        }
        return trainDTOs;
    }

    @SneakyThrows
    @GetMapping("/trains/{trainCode}")
    public ResponseEntity<?> getTrainByCode(@PathVariable String trainCode) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);
        if (optionalTrain.isPresent()) {
            TrainDTO trainDTO = customMapper.mapTrainToTrainDTO(optionalTrain.get());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(trainDTO.toString().getBytes());
            return ResponseEntity.ok().eTag(DatatypeConverter.printHexBinary(md5.digest())).body(trainDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/trains")
    public ResponseEntity<?> postTrain(@Valid @RequestBody TrainDTO trainDTO) {

        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainDTO.getTrainCode());
        Optional<Station> optionalStation = stationRepository.findByStationCode(trainDTO.getStationCode());
        Optional<Type> optionalType = typeRepository.findByTypeCode(trainDTO.getTypeCode());

        if (!optionalStation.isPresent()) {
            return ResponseEntity.badRequest().body("There is no station with that code");
        }

        if (!optionalType.isPresent()) {
            return ResponseEntity.badRequest().body("There is no type with that code");
        }

        if (!optionalTrain.isPresent()) {
            Train train = shipmentService.prepareTrain(trainDTO);

            if (train != null) {
                trainRepository.save(train);
                return ResponseEntity.ok(trainDTO);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Train with that code already exist");
        }
    }

    @SneakyThrows
    @Transactional
    @PutMapping("/trains/{trainCode}")
    public ResponseEntity<?> putTrain(@RequestHeader(value = "If-Match", required = false) String ifMatch, @Valid @RequestBody TrainPUT trainPUT, @PathVariable String trainCode) {

        if(ifMatch == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);

        if (optionalTrain.isPresent()) {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(customMapper.mapTrainToTrainDTO(optionalTrain.get()).toString().getBytes());
            Train train = shipmentService.prepareTrain(trainPUT, trainCode);
            if(!ifMatch.equals(DatatypeConverter.printHexBinary(md5.digest()))){
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
            }
            if (train != null) {
                Train save = trainRepository.save(train);
                md5.update(customMapper.mapTrainToTrainDTO(save).toString().getBytes());
                return ResponseEntity.ok().eTag(DatatypeConverter.printHexBinary(md5.digest())).body(trainPUT);
            } else {
                return ResponseEntity.badRequest().body("Station code or Type code don't match any existing entity");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    @PatchMapping("/trains/{trainCode}")
    public ResponseEntity<?> patchTrain(@RequestBody TrainPUT trainPUT, @PathVariable String trainCode) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);

        if (optionalTrain.isPresent()) {
            Train train = optionalTrain.get();
            if (trainPUT.getFullName() != null) {
                train.setFullName(trainPUT.getFullName());
            }
            if (trainPUT.getLoad() != null) {
                train.setLoad(trainPUT.getLoad());
            }
            if (trainPUT.getStationCode() != null) {
                Optional<Station> optionalStation = stationRepository.findByStationCode(trainPUT.getStationCode());
                if (optionalStation.isPresent()) {
                    train.setStation(optionalStation.get());
                } else {
                    return ResponseEntity.badRequest().body("No station with given code");
                }
            }
            trainRepository.save(train);
            return ResponseEntity.ok(customMapper.mapTrainToTrainDTO(train));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/trains/{trainCode}")
    public ResponseEntity<?> deleteTrain(@PathVariable String trainCode) {
        Optional<Train> optionalTrain = trainRepository.findByTrainCode(trainCode);

        if (optionalTrain.isPresent()) {
            trainRepository.delete(optionalTrain.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
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

    @GetMapping("/stations/{stationCode}")
    public ResponseEntity<?> getStation(@PathVariable String stationCode) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);

        if (optionalStation.isPresent()) {
            return ResponseEntity.ok(customMapper.mapStationToStationDTO(optionalStation.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/stations")
    public ResponseEntity<?> postStation(@Valid @RequestBody StationPOST stationPOST) {
        Station station = shipmentService.prepareStation(stationPOST);
        stationRepository.save(station);
        return ResponseEntity.ok(customMapper.mapStationToStationDTO(station));
    }

    @GetMapping("/station/{stationCode}/silos")
    public ResponseEntity<?> getAllSilos(@PathVariable String stationCode) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);

        if (optionalStation.isPresent()) {
            List<SiloDTO> siloDTOs = new ArrayList<>();
            optionalStation.get().getSilos().forEach(silo -> siloDTOs.add(customMapper.mapSiloToSiloDTO(silo)));
            return ResponseEntity.ok(siloDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }


    }

    @GetMapping("/station/{stationCode}/silos/{siloId}")
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

    @Transactional
    @PutMapping("/station/{stationCode}/silos/{siloId}")
    public ResponseEntity<?> putSilo(@PathVariable String stationCode, @PathVariable Long siloId, @Valid @RequestBody SiloPOST siloPOST) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);
        if (!optionalStation.isPresent()) {
            return ResponseEntity.badRequest().body("No station with that code");
        }
        Optional<Type> optionalType = typeRepository.findByTypeCode(siloPOST.getTypeCode());
        if (!optionalType.isPresent()) {
            return ResponseEntity.badRequest().body("No type with that code");
        }
        Optional<Silo> optionalSilo = siloRepository.findById(siloId);
        if (!optionalSilo.isPresent()) {
            return ResponseEntity.badRequest().body("No silo with that id");
        }

        Silo silo = optionalSilo.get();
        silo.setLoad(siloPOST.getLoad());
        silo.setStation(optionalStation.get());
        silo.setType(optionalType.get());

        siloRepository.save(silo);
        return ResponseEntity.ok(customMapper.mapSiloToSiloDTO(silo));
    }

    @DeleteMapping("/station/{stationCode}/silos/{siloId}")
    ResponseEntity<?> deleteSilo(@PathVariable String stationCode, @PathVariable Long siloId) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);

        if (optionalStation.isPresent()) {
            Optional<Silo> optionalSilo = siloRepository.findByIdAndStation(siloId, optionalStation.get());
            if (optionalSilo.isPresent()) {
                siloRepository.delete(optionalSilo.get());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/station/{stationCode}/silos")
    public ResponseEntity<?> postSilo(@PathVariable String stationCode) {
        Optional<Station> optionalStation = stationRepository.findByStationCode(stationCode);
        if (optionalStation.isPresent()) {
            Silo silo = new Silo();
            silo.setStation(optionalStation.get());
            Silo savedSilo = siloRepository.save(silo);
            (new Thread() {
                @Override
                public void run() {
                    super.run();
                    log.info("Garbage Collector - START");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Optional<Silo> optionalSilo = siloRepository.findById(savedSilo.getId());
                    log.info("Garbage Collector - CHECK IF PRESENT");
                    if (optionalSilo.isPresent()) {
                        Silo getSilo = optionalSilo.get();
                        log.info("Garbage Collector - CHECK IF NULL");
                        if (getSilo.getType() == null) {
                            log.info("Garbage Collector - DELETE");
                            siloRepository.delete(getSilo);
                        }
                    }
                }
            }).start();
            return ResponseEntity.ok(savedSilo.getId());
        } else {
            return ResponseEntity.notFound().build();
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

    @Transactional
    @PatchMapping("/types/{typeCode}")
    public ResponseEntity<?> putType(@PathVariable String typeCode, @NotBlank @RequestBody String fullName) {
        Optional<Type> optionalType = typeRepository.findByTypeCode(typeCode);

        if (optionalType.isPresent()) {
            Type type = optionalType.get();
            type.setFullName(fullName);
            typeRepository.save(type);
            return ResponseEntity.ok(type);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/transshipments")
    public List<Transshipment> getAllTransshipments() {
        return transshipmentRepository.findAll();
    }

    @GetMapping("/transshipments/{uuid}")
    public ResponseEntity<?> getTransshipment(@PathVariable String uuid) {
        Optional<Transshipment> optionalTransshipment = transshipmentRepository.findByUuid(UUID.fromString(uuid));

        if (optionalTransshipment.isPresent()) {
            return ResponseEntity.ok(optionalTransshipment.get());
        }

        return ResponseEntity.notFound().build();
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
