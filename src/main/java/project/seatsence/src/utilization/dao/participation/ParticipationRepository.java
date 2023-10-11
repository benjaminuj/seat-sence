package project.seatsence.src.utilization.dao.participation;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import project.seatsence.global.entity.BaseTimeAndStateEntity;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.Participation.Participation;
import project.seatsence.src.utilization.domain.Participation.ParticipationStatus;
import project.seatsence.src.utilization.domain.reservation.Reservation;

public interface ParticipationRepository
        extends JpaRepository<Participation, Long>, ParticipationRepositoryCustom {

    Optional<Participation> findByIdAndState(Long id, BaseTimeAndStateEntity.State state);

    Slice<Participation> findAllByUserEmailAndParticipationStatusAndStateOrderByStartScheduleDesc(
            String Email,
            ParticipationStatus participationStatus,
            BaseTimeAndStateEntity.State state,
            Pageable pageable);

    Boolean existsByUserAndReservationAndState(
            User user, Reservation reservation, BaseTimeAndStateEntity.State state);
}
