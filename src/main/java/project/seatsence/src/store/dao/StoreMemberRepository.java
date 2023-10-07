package project.seatsence.src.store.dao;

import static project.seatsence.global.entity.BaseTimeAndStateEntity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.seatsence.src.store.domain.StoreMember;
import project.seatsence.src.store.domain.StorePosition;
import project.seatsence.src.user.domain.User;

@Repository
public interface StoreMemberRepository extends JpaRepository<StoreMember, Long> {
    Boolean existsByUserIdAndState(Long userId, State state);

    Boolean existsByUserIdAndStoreIdAndState(Long userId, Long storeId, State state);

    Optional<StoreMember> findByIdAndState(Long id, State state);

    List<StoreMember> findAllByStoreIdAndPositionAndState(
            Long storeId, StorePosition position, State state);

    Optional<StoreMember> findFirstByUserIdOrderByCreatedAtAsc(Long userId);

    List<StoreMember> findAllByUserAndState(User user, State state, Pageable pageable);

    Optional<StoreMember> findByStoreIdAndUserIdAndState(Long storeId, Long userId, State state);
}
