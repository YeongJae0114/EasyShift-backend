package com.burntoburn.easyshift.entity.schedule;

import com.burntoburn.easyshift.entity.store.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "schedule_template") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 (protected)
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Builder // Lombok Builder 적용
public class ScheduleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // ID는 자동 생성되므로 Builder에서 제외
    private Long id;

    @Column(nullable = false)
    private String scheduleTemplateName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "scheduleTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 기본값 설정
    private List<ShiftTemplate> shiftTemplates = new ArrayList<>();


    // 엔티티 수정 메서드
    public void updateScheduleTemplate(String scheduleTemplateName, List<ShiftTemplate> updatedShiftTemplates) {
        this.scheduleTemplateName = scheduleTemplateName;
        this.shiftTemplates.clear();
        this.shiftTemplates.addAll(updatedShiftTemplates);
    }
}
