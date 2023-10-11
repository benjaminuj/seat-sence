package project.seatsence.src.utilization.service.walkin;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;
import static project.seatsence.src.store.domain.ReservationUnit.CHAIR;
import static project.seatsence.src.store.domain.ReservationUnit.SPACE;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.domain.ReservationUnit;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.store.service.StoreChairService;
import project.seatsence.src.store.service.StoreSpaceService;
import project.seatsence.src.utilization.dao.walkin.WalkInRepository;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.domain.UtilizationStatus;
import project.seatsence.src.utilization.domain.walkin.WalkIn;
import project.seatsence.src.utilization.dto.response.AllUtilizationsForSeatAndDateResponse;
import project.seatsence.src.utilization.service.UtilizationService;

@Service
@Transactional
@RequiredArgsConstructor
public class WalkInService {
    private final WalkInRepository walkInRepository;
    private final StoreSpaceService storeSpaceService;
    private final UtilizationService utilizationService;
    private final StoreChairService storeChairService;

    public WalkIn save(WalkIn walkIn) {
        return walkInRepository.save(walkIn);
    }

    public WalkIn findByIdAndState(Long id) {
        return walkInRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(WALK_IN_NOT_FOUND));
    }

    public ReservationUnit getUtilizationUnitOfWalkIn(WalkIn walkIn) {
        ReservationUnit utilizationUnit = null;
        if (walkIn.getUsedStoreSpace() != null) {
            utilizationUnit = SPACE;
        } else if (walkIn.getUsedStoreChair() != null) {
            utilizationUnit = CHAIR;
        }
        return utilizationUnit;
    }

    public List<WalkIn> findAllByUsedStoreSpaceAndEndScheduleIsAfterAndState(
            StoreSpace storeSpace, LocalDateTime startDateTimeToSee) {
        return walkInRepository.findAllByUsedStoreSpaceAndEndScheduleIsAfterAndState(
                storeSpace, startDateTimeToSee, ACTIVE);
    }

    public List<WalkIn> findAllByUsedStoreChairAndEndScheduleIsAfterAndState(
            StoreChair storeChair, LocalDateTime startDateTimeToSee) {
        return walkInRepository.findAllByUsedStoreChairAndEndScheduleIsAfterAndState(
                storeChair, startDateTimeToSee, ACTIVE);
    }

    public AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate
            getAllWalkInForSpaceAndDate(Long spaceId, LocalDateTime standardTime) {
        StoreSpace storeSpace = storeSpaceService.findByIdAndState(spaceId);

        List<WalkIn> allWalkInFoundBySpace =
                findAllByUsedStoreSpaceAndEndScheduleIsAfterAndState(storeSpace, standardTime);

        for (WalkIn walkIn : allWalkInFoundBySpace) {
            Utilization utilizationFoundBySpaceWalkIn =
                    utilizationService.findByWalkInIdAndState(walkIn.getId());
            if (isUtilizationStatusInUse(utilizationFoundBySpaceWalkIn)) {
                return mapUtilizationForSeatAndDateTo(walkIn);
            }
        }

        List<StoreChair> storeChairList = storeChairService.findAllByStoreSpaceAndState(storeSpace);

        for (StoreChair storeChair : storeChairList) {
            List<WalkIn> allWalkInFoundByChair =
                    findAllByUsedStoreChairAndEndScheduleIsAfterAndState(storeChair, standardTime);

            for (WalkIn walkIn : allWalkInFoundByChair) {
                Utilization utilizationFoundByChairWalkIn =
                        utilizationService.findByWalkInIdAndState(walkIn.getId());
                if (isUtilizationStatusInUse(utilizationFoundByChairWalkIn)) {
                    return mapUtilizationForSeatAndDateTo(walkIn);
                }
            }
        }
        return null; // Todo : null 대체할 방법?
    }

    public Boolean isUtilizationStatusInUse(Utilization utilization) {
        return utilization.getUtilizationStatus() == UtilizationStatus.CHECK_IN;
    }

    public AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate
            mapUtilizationForSeatAndDateTo(WalkIn walkIn) {
        return AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate.from(walkIn);
    }

    public AllUtilizationsForSeatAndDateResponse.UtilizationForSeatAndDate
            getAllWalkInForChairAndDate(Long chairId, LocalDateTime standardSchedule) {
        StoreChair storeChair = storeChairService.findByIdAndState(chairId);

        List<WalkIn> allWalkInFoundByChair =
                findAllByUsedStoreChairAndEndScheduleIsAfterAndState(storeChair, standardSchedule);

        for (WalkIn walkIn : allWalkInFoundByChair) {
            Utilization utilizationFoundByChairWalkIn =
                    utilizationService.findByWalkInIdAndState(walkIn.getId());
            if (isUtilizationStatusInUse(utilizationFoundByChairWalkIn)) {
                return mapUtilizationForSeatAndDateTo(walkIn);
            }
        }
        return null;
    }
}
