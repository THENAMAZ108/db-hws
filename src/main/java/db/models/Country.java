package db.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "countries")
@Data
public class Country {
    @Id
    @Column(name = "country_id")
    private String countryId;

    @Column(name = "name")
    private String name;

    @Column(name = "area_sqkm")
    private int areaSqkm;

    @Column(name = "population")
    private int population;
}
