package shaun.sweethome.backend.web.dto;

import jakarta.validation.constraints.*;
import java.time.*;

public class PlantDtos {

    public record PlantResponse(
            Long id,
            String name,
            LocalDate purchaseDate,
            Integer wateringCycle,
            String imageUrl,
            LocalDateTime lastWateredAt
    ) {}

    public record PlantCreateRequest(
            @NotBlank String name,
            LocalDate purchaseDate,
            @NotNull @Min(1) Integer wateringCycle
    ) {}

    public record PlantUpdateRequest(
            String name,
            LocalDate purchaseDate,
            Integer wateringCycle
    ) {}

    public record ImageUploadResponse(String imageUrl) {}
}
