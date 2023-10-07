package project.seatsence.src.utilization.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.seatsence.global.entity.BaseTimeAndStateEntity.*;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.domain.UtilizationStatus;
import project.seatsence.src.utilization.domain.reservation.Reservation;

@Repository
public interface UtilizationRepository
        extends JpaRepository<Utilization, Long>, UtilizationRepositoryCustom {
    Optional<Utilization> findByIdAndState(Long id, State state);

    List<Utilization> findAllByUtilizationStatusAndState(
            UtilizationStatus utilizationStatus, State state);

    List<Utilization> findAllByStoreAndUtilizationStatusAndState(
            Store store, UtilizationStatus utilizationStatus, State state);

    Utilization findAllByReservationAndState(Reservation reservation, State state);
}
