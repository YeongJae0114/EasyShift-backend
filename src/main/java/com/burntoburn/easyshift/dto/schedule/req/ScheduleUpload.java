package com.burntoburn.easyshift.dto.schedule.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleUpload {
    private String scheduleName;
    private Long storeId;

    private String scheduleMonth;
    private Long scheduleTemplateId;
    private String description;
    private List<ShiftDetail> shiftDetails;
}
