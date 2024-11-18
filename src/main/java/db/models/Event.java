package db.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "name")
    private String name;

    @Column(name = "eventtype")
    private String eventType;

    @Column(name = "olympic_id")
    private String olympicId;

    @Column(name = "is_team_event")
    private int isTeamEvent;

    @Column(name = "num_players_in_team")
    private Integer numPlayersInTeam;

    @Column(name = "result_noted_in")
    private String resultNotedIn;
}
