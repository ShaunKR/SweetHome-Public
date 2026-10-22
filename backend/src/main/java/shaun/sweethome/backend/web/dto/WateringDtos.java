package shaun.sweethome.backend.web.dto;

import java.time.*;

public class WateringDtos {
    public record WateringHistoryResponse(Long id, LocalDateTime wateredAt, String source) {}
    public record WaterResult(LocalDateTime lastWateredAt) {}
}
