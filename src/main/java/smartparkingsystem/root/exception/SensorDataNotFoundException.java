package smartparkingsystem.root.exception;

public class SensorDataNotFoundException extends Exception {
    private String message = "Sensor data was not found.";

    public SensorDataNotFoundException() {
    }

    public String getMessage() {
        return message;
    }
}
