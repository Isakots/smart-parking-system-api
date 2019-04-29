package smartparkingsystem.root.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SensorData implements Serializable {
    @Id
    @JsonIgnore
    private Long id;
    private Boolean busy;
    @Embedded
    private Coordinates coordinates;
    public SensorData(Long id, Boolean busy) {
        this.id = id;
        this.busy = busy;
    }
}
