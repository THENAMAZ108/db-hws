package db.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "players")
@Data
public class Player {
    @Id
    @Column(name = "player_id")
    private String playerId;

    @Column(name = "name")
    private String name;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "birthdate")
    private Date birthDate;
}
