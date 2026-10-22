package shaun.sweethome.backend.domain;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "plants")
public class Plant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "watering_cycle", nullable = false)
    private Integer wateringCycle;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "last_watered_at")
    private LocalDateTime lastWateredAt;

    protected Plant() {}

    public Plant(String name, LocalDate purchaseDate, Integer wateringCycle) {
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.wateringCycle = wateringCycle;
    }

    // ===== getter/setter (JSON 출력 + 업데이트에 필수) =====
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public Integer getWateringCycle() { return wateringCycle; }
    public void setWateringCycle(Integer wateringCycle) { this.wateringCycle = wateringCycle; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getLastWateredAt() { return lastWateredAt; }
    public void setLastWateredAt(LocalDateTime lastWateredAt) { this.lastWateredAt = lastWateredAt; }
}
