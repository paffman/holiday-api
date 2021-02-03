package de.paffman.api.holiday.model.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateTime {
    private Integer year;
    private Integer month;
    private Integer day;
}
