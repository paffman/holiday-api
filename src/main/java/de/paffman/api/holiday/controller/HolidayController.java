package de.paffman.api.holiday.controller;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.paffman.api.holiday.model.h2.HolidayEntity;
import de.paffman.api.holiday.model.input.Holiday;
import de.paffman.api.holiday.model.input.HolidayResponse;
import de.paffman.api.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/holidays")
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class HolidayController {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    HolidayRepository holidayRepository;

    @Value("${external.api.url}")
    private String externalAPI;
    @Value("${external.api.key}")
    private String externalAPIKey;


    @PostConstruct
    public void initialize() {
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    /**
     * get national holidays by county and by year
     *
     * @param country holidays for country - 2 CHARS ISO COUNTRY CODE
     * @param year    - 4 digit year
     * @return
     */
    @GetMapping(path = "/{country}/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getNationalHolidaysForYear(@PathVariable("country") String country, @PathVariable("year") Integer year) {
        List<HolidayEntity> updatedList;
        try {
            // todo: fixme on startup always empty because of H2 DB
            if (holidayRepository.findByHolidayDateContaining(year.toString()).size() > 1) {
                LOGGER.info("reading from database");
                updatedList = holidayRepository.findByHolidayDateContaining(year.toString());
                return ResponseEntity.accepted().body(mapper.writeValueAsString(updatedList));
            } else {
                updatedList = updateDatabaseData(country, year);
                if (updatedList != null) {
                    return ResponseEntity.accepted().body(mapper.writeValueAsString(updatedList));
                } else {
                    return ResponseEntity.badRequest().body("{ \"error\" : \"no data found.\"} ");
                }
            }
        } catch (Exception e) {
            LOGGER.error("getNationalHolidaysForYear:  " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * API call to external holiday API provider
     */
    private List<HolidayEntity> updateDatabaseData(String country, Integer year) {
        LOGGER.info("updating database from web api");
        try {
            List<HolidayEntity> updatedList = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            HttpEntity<String> httpHeader = new HttpEntity<>(headers);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(externalAPI)
                    .queryParam("api_key", externalAPIKey)
                    .queryParam("country", country)
                    .queryParam("year", year);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpHeader, String.class).getBody();
            JsonNode root = mapper.readTree(response);
            HolidayResponse holidayResponse = mapper.treeToValue(root.get("response"), HolidayResponse.class);

            for (Holiday holiday : holidayResponse.getHolidays()) {
                HolidayEntity entity = mapToEntity(holiday);
                holidayRepository.saveAndFlush(entity);
                updatedList.add(entity);
            }

            return updatedList;

        } catch (Exception e) {
            LOGGER.error("updateDatabaseData:  " + e.getMessage());
            return null;
        }
    }

    /**
     * map Holiday model to jpa entity
     *
     * @param holiday model
     * @return entity of model
     */
    private HolidayEntity mapToEntity(Holiday holiday) {
        HolidayEntity holidayEntity = new HolidayEntity();
        holidayEntity.setHolidayDate(holiday.getDate().getIso());
        holidayEntity.setName(holiday.getName());
        for (String hs : holiday.getType()) {
            if (hs.equalsIgnoreCase("National holiday")) {
                holidayEntity.setIsPublic(true);
            }
        }
        return holidayEntity;
    }
}
