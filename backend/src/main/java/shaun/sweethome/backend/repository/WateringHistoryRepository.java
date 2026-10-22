package shaun.sweethome.backend.repository;

import shaun.sweethome.backend.domain.WateringHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WateringHistoryRepository extends JpaRepository<WateringHistory, Long> {
    List<WateringHistory> findByPlantIdOrderByWateredAtDesc(Long plantId);
}
