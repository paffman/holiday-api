package de.paffman.api.holiday.model.h2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "HOLIDAYS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "holiday_date")
    private String holidayDate;

    @Column(name = "name")
    private String name;

    @Column(name = "is_public")
    private Boolean isPublic;
}
