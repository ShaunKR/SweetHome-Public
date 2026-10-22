package shaun.sweethome.backend.domain;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name = "watering_history")
public class WateringHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="plant_id", nullable = false)
    private Long plantId;

    @Column(name="watered_at", nullable = false)
    private LocalDateTime wateredAt;

    @Column(name="source", length = 30)
    private String source;

    protected WateringHistory() {}

    public WateringHistory(Long plantId, LocalDateTime wateredAt, String source) {
        this.plantId = plantId;
        this.wateredAt = wateredAt;
        this.source = source;
    }

    public Long getId() { return id; }
    public Long getPlantId() { return plantId; }
    public LocalDateTime getWateredAt() { return wateredAt; }
    public String getSource() { return source; }
}
