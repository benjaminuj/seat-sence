package project.seatsence.src.utilization.domain.walkin;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import project.seatsence.global.entity.BaseEntity;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.user.domain.User;

@Getter
@Entity(name = "WalkIn")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class WalkIn extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "used_store_space_id")
    private StoreSpace usedStoreSpace; // 스페이스 바로사용일 때만, 해당 스페이스

    @Nullable
    @ManyToOne
    @JoinColumn(name = "used_store_chair_id")
    private StoreChair usedStoreChair; // 의자 바로사용일 때만, 해당 의자

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull private LocalDateTime startSchedule;
    @NotNull private LocalDateTime endSchedule;

    @Builder
    public WalkIn(
            Store store,
            StoreSpace usedStoreSpace,
            StoreChair usedStoreChair,
            User user,
            LocalDateTime startSchedule,
            LocalDateTime endSchedule) {
        this.store = store;
        this.usedStoreSpace = usedStoreSpace;
        this.usedStoreChair = usedStoreChair;
        this.user = user;
        this.startSchedule = startSchedule;
        this.endSchedule = endSchedule;
    }
}
