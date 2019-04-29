package smartparkingsystem.root.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smartparkingsystem.root.domain.SensorData;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}
