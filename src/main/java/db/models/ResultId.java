package db.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResultId implements Serializable {
    private String eventId;
    private String playerId;
}
