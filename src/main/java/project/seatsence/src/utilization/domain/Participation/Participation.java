package project.seatsence.src.utilization.domain.Participation;

import java.time.LocalDateTime;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import project.seatsence.global.entity.BaseEntity;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.walkin.WalkIn;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "walk_in_id")
    private WalkIn walkIn;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'UPCOMING_PARTICIPATION'")
    private ParticipationStatus participationStatus;

    @NotNull private LocalDateTime startSchedule;

    @Builder
    public Participation(
            @Nullable Reservation reservation,
            @Nullable WalkIn walkIn,
            User user,
            Store store,
            ParticipationStatus participationStatus,
            LocalDateTime startSchedule) {
        this.reservation = reservation;
        this.walkIn = walkIn;
        this.user = user;
        this.store = store;
        this.participationStatus = participationStatus;
        this.startSchedule = startSchedule;
    }

    public void cancelParticipation() {
        participationStatus = ParticipationStatus.CANCELED;
    }
}
