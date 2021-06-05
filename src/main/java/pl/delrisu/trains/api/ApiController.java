package pl.delrisu.trains.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import pl.delrisu.trains.model.DTO.TrainDTO;
import pl.delrisu.trains.model.Silo;
import pl.delrisu.trains.model.Station;
import pl.delrisu.trains.model.Train;
import pl.delrisu.trains.model.Type;
import pl.delrisu.trains.repository.*;
import pl.delrisu.trains.service.ShipmentService;

import javax.validation.Valid;
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

    @GetMapping("/trains")
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
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
    public ResponseEntity<?> postStation(@RequestBody Station station) {
        return ResponseEntity.ok(stationRepository.save(station));
    }

    @PostMapping("/station/{stationCode}/silo")
    public ResponseEntity<?> postSilo(@PathVariable String stationCode, @RequestBody Silo silo) {
        Silo siloRet = shipmentService.prepareSilo(stationCode, silo);

        if (siloRet != null) {
            return ResponseEntity.ok(siloRet);
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
