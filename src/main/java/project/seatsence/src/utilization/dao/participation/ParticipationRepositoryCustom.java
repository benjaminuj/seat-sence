package project.seatsence.src.utilization.dao.participation;

import project.seatsence.global.entity.BaseTimeAndStateEntity;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.utilization.domain.walkin.WalkIn;

public interface ParticipationRepositoryCustom {
    Boolean existsByUserAndWalkInAndState(
            User user, WalkIn walkIn, BaseTimeAndStateEntity.State state);
}
