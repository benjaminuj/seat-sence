package project.seatsence.src.utilization.dao.reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.seatsence.global.entity.BaseTimeAndStateEntity.*;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.reservation.Reservation;
import project.seatsence.src.utilization.domain.reservation.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /** Reservation (Common (User + Admin)) */
    Reservation save(Reservation reservation);

    Optional<Reservation> findByIdAndState(Long id, State state);

    /** User Reservation */
    Slice<Reservation> findAllByUserAndReservationStatusAndStateOrderByStartScheduleDesc(
            User user, ReservationStatus reservationStatus, State state, Pageable pageable);

    List<Reservation> findAllByUserAndReservationStatusAndState(
            User user, ReservationStatus reservationStatus, State state);

    List<Reservation>
            findAllByReservedStoreChairAndReservationStatusInAndEndScheduleIsAfterAndEndScheduleIsBeforeAndState(
                    StoreChair storeChair,
                    List<ReservationStatus> reservationStatuses,
                    LocalDateTime startDateTimeToSee,
                    LocalDateTime limit,
                    State state);

    List<Reservation>
            findAllByReservedStoreSpaceAndReservationStatusInAndEndScheduleIsAfterAndEndScheduleIsBeforeAndState(
                    StoreSpace storeSpace,
                    List<ReservationStatus> reservationStatuses,
                    LocalDateTime startDateTimeToSee,
                    LocalDateTime limit,
                    State state);

    /** Admin Reservation */
    List<Reservation> findAllByStoreIdAndState(Long storeId, State state);

    List<Reservation> findAllByStoreIdAndReservationStatusAndState(
            Long storeId, ReservationStatus reservationStatus, State state);

    List<Reservation> findAllByStoreIdAndReservationStatusNotAndState(
            Long storeId, ReservationStatus reservationStatus, State state);
}
