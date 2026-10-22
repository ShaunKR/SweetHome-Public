package shaun.sweethome.backend.service;

import shaun.sweethome.backend.domain.*;
import shaun.sweethome.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final WateringHistoryRepository historyRepository;

    public PlantService(PlantRepository plantRepository,
                        WateringHistoryRepository historyRepository) {
        this.plantRepository = plantRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public LocalDateTime water(Long plantId, String source) {
        historyRepository.save(new WateringHistory(plantId, LocalDateTime.now(), source));

        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setLastWateredAt(LocalDateTime.now());
        // JPA dirty checking으로 자동 update
        return plant.getLastWateredAt();
    }
}
