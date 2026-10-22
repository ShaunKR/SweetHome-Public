package shaun.sweethome.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import shaun.sweethome.backend.domain.Plant;
import shaun.sweethome.backend.repository.*;
import shaun.sweethome.backend.service.PlantService;
import shaun.sweethome.backend.web.dto.PlantDtos.*;
import shaun.sweethome.backend.web.dto.WateringDtos.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "식물관리 API", description = "식물 관리 API")
@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantRepository plantRepository;
    private final WateringHistoryRepository historyRepository;
    private final PlantService plantService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public PlantController(PlantRepository plantRepository,
                           WateringHistoryRepository historyRepository,
                           PlantService plantService) {
        this.plantRepository = plantRepository;
        this.historyRepository = historyRepository;
        this.plantService = plantService;
    }

    // ===== List =====
    @GetMapping
    public List<PlantResponse> list() {
        return plantRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ===== Detail =====
    @GetMapping("/{id}")
    public PlantResponse detail(@PathVariable Long id) {
        Plant plant = plantRepository.findById(id).orElseThrow();
        return toDto(plant);
    }

    // ===== Create =====
    @PostMapping
    public PlantResponse create(@RequestBody @Valid PlantCreateRequest req) {
        Plant plant = new Plant(req.name(), req.purchaseDate(), req.wateringCycle());
        plantRepository.save(plant);
        return toDto(plant);
    }

    // ===== Update (PATCH) =====
    @PatchMapping("/{id}")
    public PlantResponse update(@PathVariable Long id, @RequestBody PlantUpdateRequest req) {
        Plant plant = plantRepository.findById(id).orElseThrow();

        if (req.name() != null) plant.setName(req.name());
        if (req.purchaseDate() != null) plant.setPurchaseDate(req.purchaseDate());
        if (req.wateringCycle() != null) plant.setWateringCycle(req.wateringCycle());

        // save 없어도 되지만 명시적으로 해도 OK
        plantRepository.save(plant);
        return toDto(plant);
    }

    // ===== Delete =====
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        plantRepository.deleteById(id);
    }

    // ===== Upload image =====
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageUploadResponse uploadImage(@PathVariable Long id,
                                           @RequestPart("file") MultipartFile file) throws Exception {
        Plant plant = plantRepository.findById(id).orElseThrow();

        if (file.isEmpty()) throw new IllegalArgumentException("file is empty");

        // 확장자 안전 처리(최소한)
        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot).toLowerCase(Locale.ROOT);

        String filename = id + "_" + UUID.randomUUID() + ext;

        Path dir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        Path saved = dir.resolve(filename);
        Files.copy(file.getInputStream(), saved, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/uploads/" + filename; // ✅ 운영(B)에서 그대로 사용 가능(상대경로)
        plant.setImageUrl(imageUrl);
        plantRepository.save(plant);

        return new ImageUploadResponse(imageUrl);
    }

    // ===== Water action =====
//    @PostMapping("/{id}/water")
//    public WaterResult water(@PathVariable Long id) {
//        LocalDateTime updated = plantService.water(id, "UI");
//        return new WaterResult(updated);
//    }

    @PostMapping("/{id}/water")
    public WaterResult water(
            @PathVariable Long id,
            @RequestParam(defaultValue = "UI") String source
    ) {
        LocalDateTime updated = plantService.water(id, source);
        return new WaterResult(updated);
    }


    // ===== History =====
    @GetMapping("/{id}/history")
    public List<WateringHistoryResponse> history(@PathVariable Long id) {
        return historyRepository.findByPlantIdOrderByWateredAtDesc(id).stream()
                .map(h -> new WateringHistoryResponse(h.getId(), h.getWateredAt(), h.getSource()))
                .collect(Collectors.toList());
    }

    private PlantResponse toDto(Plant p) {
        return new PlantResponse(
                p.getId(),
                p.getName(),
                p.getPurchaseDate(),
                p.getWateringCycle(),
                p.getImageUrl(),
                p.getLastWateredAt()
        );
    }
}
