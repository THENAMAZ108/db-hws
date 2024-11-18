package db.models;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "results")
@IdClass(ResultId.class)
@Data
public class Result {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Id
    @Column(name = "player_id")
    private String playerId;

    @Column(name = "medal")
    private String medal;

    @Column(name = "result")
    private Double result;
}
