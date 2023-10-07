package project.seatsence.src.utilization.dao.walkin;

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
import project.seatsence.src.utilization.domain.walkin.WalkIn;

@Repository
public interface WalkInRepository extends JpaRepository<WalkIn, Long> {
    WalkIn save(WalkIn walkIn);

    Optional<WalkIn> findByIdAndState(Long id, State state);

    Slice<WalkIn> findAllByUserEmailAndStateOrderByStartScheduleDesc(
            String Email, State state, Pageable pageable);

    List<WalkIn> findAllByUsedStoreSpaceAndEndScheduleIsAfterAndState(
            StoreSpace storeSpace, LocalDateTime startDateTimeToSee, State state);

    List<WalkIn> findAllByUsedStoreChairAndEndScheduleIsAfterAndState(
            StoreChair storeChair, LocalDateTime startDateTimeToSee, State state);

    List<WalkIn> findByStoreIdAndEndScheduleAfterAndUsedStoreSpaceIdIsNotNullAndState(
            Long storeId, LocalDateTime endSchedule, State state);
}
