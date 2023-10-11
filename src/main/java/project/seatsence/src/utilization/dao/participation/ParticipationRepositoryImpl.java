package project.seatsence.src.utilization.dao.participation;

import static project.seatsence.src.utilization.domain.Participation.QParticipation.participation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.seatsence.global.entity.BaseTimeAndStateEntity;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.walkin.WalkIn;

@RequiredArgsConstructor
public class ParticipationRepositoryImpl implements ParticipationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Boolean existsByUserAndWalkInAndState(
            User user, WalkIn walkIn, BaseTimeAndStateEntity.State state) {

        Boolean exists =
                queryFactory
                                .selectOne()
                                .from(participation)
                                .where(
                                        participation.user.eq(user),
                                        participation.walkIn.eq(walkIn),
                                        participation.state.eq(state))
                                .fetchFirst()
                        != null;

        return exists;
    }
}
