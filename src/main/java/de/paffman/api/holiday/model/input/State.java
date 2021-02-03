package de.paffman.api.holiday.model.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private Integer id;
    private String abbrev;
    private String name;
    private String exception;
    private String iso;

    public State(String singleString) {
        this.abbrev = singleString;
    }
}
