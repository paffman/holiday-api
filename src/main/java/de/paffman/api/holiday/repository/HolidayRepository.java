package de.paffman.api.holiday.repository;

import de.paffman.api.holiday.model.h2.HolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<HolidayEntity, Long> {

    List<HolidayEntity> findByHolidayDateContaining(String year);
}
