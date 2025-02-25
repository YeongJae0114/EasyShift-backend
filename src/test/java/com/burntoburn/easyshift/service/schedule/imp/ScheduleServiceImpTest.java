package com.burntoburn.easyshift.service.schedule.imp;

import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ScheduleRequest;
import com.burntoburn.easyshift.dto.schedule.req.scheduleCreate.ShiftRequest;
import com.burntoburn.easyshift.entity.schedule.Schedule;
import com.burntoburn.easyshift.entity.schedule.ScheduleStatus;
import com.burntoburn.easyshift.entity.schedule.Shift;
import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.templates.ScheduleTemplate;
import com.burntoburn.easyshift.entity.templates.ShiftTemplate;
import com.burntoburn.easyshift.repository.schedule.ScheduleRepository;
import com.burntoburn.easyshift.repository.schedule.ScheduleTemplateRepository;
import com.burntoburn.easyshift.repository.schedule.ShiftRepository; // ✅ ShiftRepository 추가
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.service.schedule.ScheduleService;
import java.time.LocalTime;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleServiceImpTest {

    @Autowired
    private ScheduleService scheduleService;

    @MockitoBean
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @MockitoBean
    private ScheduleRepository scheduleRepository;

    @MockitoBean
    private StoreRepository storeRepository;

    @MockitoBean
    private ShiftRepository shiftRepository; // ✅ ShiftRepository 추가

    private Store store;
    private ScheduleTemplate scheduleTemplate;
    private Schedule existingSchedule;

    @BeforeEach
    void setUp() {
        store = Store.builder()
                .id(1L)
                .build();

        scheduleTemplate = ScheduleTemplate.builder()
                .id(1L)
                .scheduleTemplateName("Morning Shift")
                .store(store)
                .build();

        // ✅ ShiftTemplate 추가
        ShiftTemplate shift1 = ShiftTemplate.builder()
                .id(1L)
                .shiftTemplateName("Morning Shift")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        ShiftTemplate shift2 = ShiftTemplate.builder()
                .id(2L)
                .shiftTemplateName("Evening Shift")
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(22, 0))
                .build();

        scheduleTemplate.getShiftTemplates().addAll(List.of(shift1, shift2));

        existingSchedule = Schedule.builder()
                .id(1L)
                .scheduleName("March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .build();
    }


    @Test
    @DisplayName("스케줄 생성 테스트 - 빈 Shift 포함 검증")
    void createScheduleWithShifts() {
        // Given
        int daysInMonth = YearMonth.of(2024, 3).lengthOfMonth(); // 한 달의 일 수 (3월 → 31일)

        List<ShiftRequest> shiftRequests = List.of(
                new ShiftRequest(1L, 3), // ✅ Morning Shift에 3명 배정
                new ShiftRequest(2L, 2)  // ✅ Evening Shift에 2명 배정
        );

        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .scheduleTemplateId(1L)
                .shiftDetails(shiftRequests) // ✅ ShiftRequest 추가
                .build();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(scheduleTemplateRepository.findById(1L)).thenReturn(Optional.of(scheduleTemplate));
        when(scheduleRepository.saveAndFlush(any(Schedule.class))).thenReturn(existingSchedule);

        // When
        Schedule createdSchedule = scheduleService.createSchedule(1L, request);

        // Then
        assertNotNull(createdSchedule, "생성된 Schedule이 null이면 안 됩니다.");
        assertEquals("March Schedule", createdSchedule.getScheduleName());
        assertEquals(YearMonth.of(2024, 3), createdSchedule.getScheduleMonth());

        // ✅ 올바른 Shift 개수 계산
        int expectedShifts = shiftRequests.stream()
                .mapToInt(shiftRequest -> shiftRequest.getExpectedWorkers() * daysInMonth) // 각 ShiftTemplate에 대해 한 달 동안 생성된 Shift 개수
                .sum();

        assertNotNull(createdSchedule.getShifts(), "Shifts 객체가 null이면 안 됩니다.");
        assertFalse(createdSchedule.getShifts().getList().isEmpty(), "Shift 리스트가 비어있으면 안 됩니다.");
        assertEquals(expectedShifts, createdSchedule.getShifts().getList().size(), "Shift 개수가 일치해야 합니다.");

        // ✅ Shift 저장 여부 검증
        verify(shiftRepository, times(1)).saveAll(anyList()); // ShiftRepository가 1번 호출되었는지 확인

        // ✅ Schedule 저장 여부 검증
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }


    @Test
    @DisplayName("스케줄 삭제 테스트")
    void deleteSchedule() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        doNothing().when(scheduleRepository).delete(existingSchedule);

        // When
        scheduleService.deleteSchedule(1L);

        // Then
        verify(scheduleRepository, times(1)).delete(existingSchedule);
    }

    @Test
    @DisplayName("스케줄 수정 테스트")
    void updateSchedule() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .id(1L)
                .scheduleName("Updated March Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .scheduleStatus(ScheduleStatus.PENDING)
                .store(store)
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(updatedSchedule);

        // When
        Schedule result = scheduleService.updateSchedule(1L, request);

        // Then
        assertNotNull(result);
        assertEquals("Updated March Schedule", result.getScheduleName());

        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 삭제 시 예외 발생")
    void deleteSchedule_NotFound() {
        // Given
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.deleteSchedule(1L));
    }

    @Test
    @DisplayName("존재하지 않는 스케줄 수정 시 예외 발생")
    void updateSchedule_NotFound() {
        // Given
        ScheduleRequest request = ScheduleRequest.builder()
                .scheduleName("Updated Schedule")
                .scheduleMonth(YearMonth.of(2024, 3))
                .build();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> scheduleService.updateSchedule(1L, request));
    }
}
