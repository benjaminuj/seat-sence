package project.seatsence.src.utilization.dao;

import static project.seatsence.src.utilization.domain.QUtilization.utilization;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.seatsence.global.entity.BaseTimeAndStateEntity.*;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.utilization.domain.QUtilization;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.domain.UtilizationStatus;

@RequiredArgsConstructor
@Repository
public class UtilizationRepositoryImpl implements UtilizationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Utilization> findSeatCurrentlyInUseByUnit(
            Long storeSpaceId,
            ReservationUnit utilizationUnit,
            UtilizationStatus utilizationStatus1,
            UtilizationStatus utilizationStatus2,
            State state) {
        List<Utilization> allUtilization =
                queryFactory
                        .selectFrom(utilization)
                        .where(
                                utilization
                                        .storeSpace
                                        .id
                                        .eq(storeSpaceId)
                                        .and(utilization.utilizationUnit.eq(utilizationUnit))
                                        .and(utilization.state.eq(state))
                                        .and(
                                                (utilization.utilizationStatus.eq(
                                                                utilizationStatus1))
                                                        .or(
                                                                utilization.utilizationStatus.eq(
                                                                        utilizationStatus2))))
                        .fetch();
        return allUtilization;
    }

    @Override
    public Utilization findByWalkInIdAndState(Long walkInId, State state) {
        Utilization utilization =
                queryFactory
                        .selectFrom(QUtilization.utilization)
                        .where(
                                QUtilization.utilization
                                        .walkIn
                                        .id
                                        .eq(walkInId)
                                        .and(QUtilization.utilization.state.eq(state)))
                        .fetchOne();
        return utilization;
    }
}
