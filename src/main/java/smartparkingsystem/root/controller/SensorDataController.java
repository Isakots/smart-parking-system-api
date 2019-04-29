package smartparkingsystem.root.controller;

import com.grum.geocalc.Coordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smartparkingsystem.root.controller.dto.DataDTO;
import smartparkingsystem.root.controller.util.Constants;
import smartparkingsystem.root.domain.SensorData;
import smartparkingsystem.root.exception.SensorDataNotFoundException;
import smartparkingsystem.root.repository.SensorDataRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class SensorDataController {

    private SensorDataRepository sensorDataRepository;

    Logger logger = LoggerFactory.getLogger(SensorDataController.class);

    @Autowired
    public SensorDataController(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    @GetMapping(Constants.SENSOR_DATA_ENDPOINT)
    public ResponseEntity<SensorData> getData(@RequestHeader(defaultValue = Constants.DEFAULT_DISTANCE) int range,
                                              @RequestParam double lon,
                                              @RequestParam double lat) throws SensorDataNotFoundException {

        Point actualPosition = Point.at(Coordinate.fromDegrees(lat), Coordinate.fromDegrees(lon));

        // this solution is really ugly.. I should check the distance in SQL query but this time it's enough.
        List<SensorData> result = Optional.of(sensorDataRepository.findAll()).orElseThrow(SensorDataNotFoundException::new);
        SensorData closestData = result.stream()
                .filter(data -> !data.getBusy())
                .filter(data -> {
                    Point forePosition = Point.at(Coordinate.fromDegrees(data.getCoordinates().getLatitude()),
                            Coordinate.fromDegrees(data.getCoordinates().getLongitude()));
                    // Returns the distance in meters
                    double distance = EarthCalc.harvesineDistance(actualPosition, forePosition);
                    return distance < range;
                })
                .min((data1, data2) -> {
                    Point forePosition1 = Point.at(Coordinate.fromDegrees(data1.getCoordinates().getLatitude()),
                            Coordinate.fromDegrees(data1.getCoordinates().getLongitude()));
                    Point forePosition2 = Point.at(Coordinate.fromDegrees(data2.getCoordinates().getLatitude()),
                            Coordinate.fromDegrees(data2.getCoordinates().getLongitude()));
                    double distance1 = EarthCalc.harvesineDistance(actualPosition, forePosition1);
                    double distance2 = EarthCalc.harvesineDistance(actualPosition, forePosition2);
                    return Double.compare(distance1, distance2);
                })
                .orElseThrow(SensorDataNotFoundException::new);

        return new ResponseEntity<>(
                closestData, null, HttpStatus.OK);
    }

    @PostMapping(Constants.SENSOR_DATA_ENDPOINT)
    public ResponseEntity addData(@RequestBody DataDTO data) {
        logger.info("POST is called.");
        if (data.getWemosid() == null)
            return ResponseEntity.badRequest().body("ID is missing!");

        SensorData sensorData = new SensorData(data.getWemosid(), data.getBusy());

        sensorDataRepository.save(sensorData);
        return ResponseEntity.ok("Create data is successful.");
    }


    @PutMapping(Constants.SENSOR_DATA_ENDPOINT)
    public ResponseEntity updateData(@RequestBody DataDTO data) throws SensorDataNotFoundException {
        logger.info("PUT is called.");
        if (data.getWemosid() == null)
            return ResponseEntity.badRequest().body("ID is missing!");
        SensorData sensorData = sensorDataRepository.findById(data.getWemosid()).orElseThrow(SensorDataNotFoundException::new);

        sensorDataRepository.save(sensorData);
        return ResponseEntity.ok("Update data is successful.");
    }


}
