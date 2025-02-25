package com.burntoburn.easyshift.dto.store.res;

import com.burntoburn.easyshift.dto.schedule.res.ScheduleDetailDTO;
import com.burntoburn.easyshift.dto.schedule.res.ScheduleSummaryDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class StoreScheduleResponseDTO {

    private Long storeId;
    private String storeName;
    private List<ScheduleSummaryDTO> schedules;
    private ScheduleDetailDTO selectedSchedule;

}
