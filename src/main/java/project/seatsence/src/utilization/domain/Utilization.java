package project.seatsence.src.utilization.domain;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import project.seatsence.global.entity.BaseEntity;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.utilization.domain.Participation.Participation;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.walkin.WalkIn;

/** Utilization(이용) = Reservation(예약) + Walk-In(뱌로사용) */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Utilization extends BaseEntity {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "store_space_id")
    private StoreSpace storeSpace; // 의자든 스페이스든 상관없이 무조건 해당되는 스페이스 맵핑

    @Nullable
    @ManyToOne
    @JoinColumn(name = "used_store_chair_id")
    private StoreChair usedStoreChair; // 의자 이용일 경우에만, 이용된 의자 식별자 값

    @Nullable
    @OneToOne
    @JoinColumn(name = "walk_in_id")
    private WalkIn walkIn;

    @Nullable
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Nullable
    @OneToOne
    @JoinColumn(name = "participation_id")
    private Participation participation;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'CHECK_IN'")
    private UtilizationStatus utilizationStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationUnit utilizationUnit;

    @CreationTimestamp
    @Column(updatable = false)
    @NotNull
    private LocalDateTime startSchedule; // 이용 (예약 or 바로사용) 시작시간

    @NotNull private LocalDateTime endSchedule; // 이용 끝시간

    public void forceCheckOut() {
        this.utilizationStatus = UtilizationStatus.FORCED_CHECK_OUT;
        this.endSchedule = LocalDateTime.now();
    }
}
