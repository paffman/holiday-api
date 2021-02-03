package de.paffman.api.holiday.model.input;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Holiday {
    private String name;
    private String description;
    private Country country;
    private Date date;
    private List<String> type;
    private String locations;
    private List<State> states;
}
