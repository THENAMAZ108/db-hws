package db.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "olympics")
@Data
public class Olympic {
    @Id
    @Column(name = "olympic_id")
    private String olympicId;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "city")
    private String city;

    @Column(name = "year")
    private int year;

    @Column(name = "startdate")
    private Date startDate;

    @Column(name = "enddate")
    private Date endDate;
}
