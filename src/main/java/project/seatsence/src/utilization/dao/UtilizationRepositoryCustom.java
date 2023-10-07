package project.seatsence.src.utilization.dao;

import java.util.List;
import project.seatsence.global.entity.BaseTimeAndStateEntity;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.domain.UtilizationStatus;

public interface UtilizationRepositoryCustom {
    public List<Utilization> findSeatCurrentlyInUseByUnit(
            Long storeSpaceId,
            ReservationUnit utilizationUnit,
            UtilizationStatus utilizationStatus1,
            UtilizationStatus utilizationStatus2,
            BaseTimeAndStateEntity.State state);

    public Utilization findByWalkInIdAndState(Long walkInId, BaseTimeAndStateEntity.State state);
}
