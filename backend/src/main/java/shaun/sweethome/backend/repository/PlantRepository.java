package shaun.sweethome.backend.repository;

import shaun.sweethome.backend.domain.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, Long> {}
