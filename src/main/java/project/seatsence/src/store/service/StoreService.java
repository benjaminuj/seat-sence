package project.seatsence.src.store.service;

import static project.seatsence.global.code.ResponseCode.STORE_NOT_FOUND;
import static project.seatsence.global.code.ResponseCode.STORE_SORT_FIELD_NOT_FOUND;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.ACTIVE;
import static project.seatsence.src.store.domain.Day.*;
import static project.seatsence.src.store.domain.Day.SAT;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.global.utils.EnumUtils;
import project.seatsence.src.store.dao.StoreMemberRepository;
import project.seatsence.src.store.dao.StoreRepository;
import project.seatsence.src.store.domain.*;
import project.seatsence.src.store.dto.request.admin.basic.StoreBasicInformationRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreIsClosedTodayRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreNewBusinessInformationRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreOperatingTimeRequest;
import project.seatsence.src.store.dto.response.LoadSeatStatisticsInformationOfStoreResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreBasicInformationResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreNewBusinessInformationResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreOwnedStoreResponse;
import project.seatsence.src.user.domain.User;
import project.seatsence.src.user.service.UserService;
import project.seatsence.src.utilization.domain.Utilization;
import project.seatsence.src.utilization.domain.UtilizationStatus;
import project.seatsence.src.utilization.service.UtilizationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final UserService userService;
    private final StoreRepository storeRepository;
    private final StoreMemberRepository storeMemberRepository;
    private final S3Service s3Service;
    private final StoreImageService storeImageService;
    private final StoreChairService storeChairService;
    private final UtilizationService utilizationService;

    private static final String STORE_IMAGE_S3_PATH = "store-images";

    public StoreOwnedStoreResponse findAllOwnedStore(String userEmail) {
        User user = userService.findByEmailAndState(userEmail);
        List<StoreMember> storeMemberList =
                storeMemberRepository.findAllByUserAndState(user, ACTIVE);
        List<Long> storeIds =
                storeMemberList.stream()
                        .map(storeMember -> storeMember.getStore().getId())
                        .collect(Collectors.toList());
        List<Store> storeList = storeRepository.findAllByIdInAndState(storeIds, ACTIVE);
        List<StoreOwnedStoreResponse.StoreResponse> storeResponseList =
                storeList.stream()
                        .map(
                                store ->
                                        new StoreOwnedStoreResponse.StoreResponse(
                                                store.getId(),
                                                store.getStoreName(),
                                                store.getIntroduction(),
                                                store.getMainImage(),
                                                isOpenNow(store),
                                                store.isClosedToday()))
                        .collect(Collectors.toList());
        return new StoreOwnedStoreResponse(storeResponseList);
    }

    @Transactional
    public void updateBasicInformation(
            StoreBasicInformationRequest request, Long storeId, List<MultipartFile> files)
            throws IOException {
        Store store =
                storeRepository
                        .findByIdAndState(storeId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));
        store.updateBasicInformation(request);
        // 가장 첫번째 이미지 대표 이미지로 업데이트
        List<String> uploads = s3Service.upload(files, STORE_IMAGE_S3_PATH, store.getId());
        store.updateMainImage(uploads.get(0));
        // 나머지 이미지 업로드
        storeImageService.saveStoreImageList(store, uploads);
    }

    public Store findByIdAndState(Long id) {
        return storeRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));
    }

    public StoreBasicInformationResponse getStoreBasicInformation(Long storeId) {
        Store store = findByIdAndState(storeId);
        List<String> storeImages = new ArrayList<>();
        storeImages.add(store.getMainImage());
        storeImages.addAll(storeImageService.getStoreImages(store));
        return StoreBasicInformationResponse.of(store, storeImages);
    }

    // 사업자 등록번호 추가
    public StoreNewBusinessInformationResponse adminNewBusinessInformation(
            String userEmail,
            StoreNewBusinessInformationRequest storeNewBusinessInformationRequest) {
        User user = userService.findByEmailAndState(userEmail);
        LocalDate openDate =
                LocalDate.parse(
                        storeNewBusinessInformationRequest.getOpenDate(),
                        DateTimeFormatter.ISO_DATE);
        Store newStore =
                new Store(
                        user,
                        storeNewBusinessInformationRequest.getBusinessRegistrationNumber(),
                        openDate,
                        storeNewBusinessInformationRequest.getAdminName(),
                        storeNewBusinessInformationRequest.getStoreName(),
                        storeNewBusinessInformationRequest.getAddress(),
                        storeNewBusinessInformationRequest.getDetailAddress());

        // OWNER 권한
        StoreMember newStoreMember =
                StoreMember.builder()
                        .user(user)
                        .store(newStore)
                        .position(StorePosition.OWNER)
                        .permissionByMenu(
                                "{\"storeStatus\":true,\"seatSetting\":true,\"storeStatistics\":true,\"storeSetting\":true}")
                        .build();

        storeRepository.save(newStore);
        storeMemberRepository.save(newStoreMember);

        return new StoreNewBusinessInformationResponse(newStore.getId());
    }

    @Transactional
    public void updateOperatingTime(StoreOperatingTimeRequest request, Long storeId) {
        Store store =
                storeRepository
                        .findByIdAndState(storeId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));
        store.updateOperatingTime(request);
    }

    @Transactional
    public void delete(Long storeId) {
        Store store =
                storeRepository
                        .findByIdAndState(storeId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));
        store.delete();
    }

    public Page<Store> findAllByState(String category, Pageable pageable) {
        try {
            for (Sort.Order order : pageable.getSort()) {
                String sortField = order.getProperty();
                Store.class.getDeclaredField(sortField);
            }
            if (category == null) {
                return storeRepository.findAllByState(ACTIVE, pageable);
            } else {
                Category categoryEnum = EnumUtils.getEnumFromString(category, Category.class);
                return storeRepository.findAllByCategoryAndState(categoryEnum, ACTIVE, pageable);
            }
        } catch (NoSuchFieldException e) {
            throw new BaseException(STORE_SORT_FIELD_NOT_FOUND);
        }
    }

    public Boolean isOpenNow(Store store) {
        // today's day of week
        Calendar cal = Calendar.getInstance();
        int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        log.info(String.valueOf(todayDayOfWeek)); // 오늘 요일
        String dayOff = store.getDayOff();

        // 휴무일이 있는 가게의 경우 휴무일 판단 먼저
        if (dayOff != null) {
            List<Day> dayOffList = EnumUtils.getEnumListFromString(dayOff, Day.class);
            for (Day day : dayOffList) {
                // 휴무일일 경우
                if (todayDayOfWeek == day.getDayOfWeek()) {
                    return false;
                }
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now();
        LocalTime openTime;
        LocalTime closeTime;
        // check if currentTime is between openTime and closeTime
        if (todayDayOfWeek == MON.getDayOfWeek()) {
            if (store.getMonOpenTime() == null || store.getMonCloseTime() == null)
                return null; // 오늘이 월요일인데 월요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getMonOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getMonCloseTime(), formatter);
        } else if (todayDayOfWeek == TUE.getDayOfWeek()) {
            if (store.getTueOpenTime() == null || store.getTueCloseTime() == null)
                return null; // 오늘이 화요일인데 화요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getTueOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getTueCloseTime(), formatter);
        } else if (todayDayOfWeek == WED.getDayOfWeek()) {
            if (store.getWedOpenTime() == null || store.getWedCloseTime() == null)
                return null; // 오늘이 수요일인데 수요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getWedOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getWedCloseTime(), formatter);
        } else if (todayDayOfWeek == THU.getDayOfWeek()) {
            if (store.getThuOpenTime() == null || store.getThuCloseTime() == null)
                return null; // 오늘이 목요일인데 목요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getThuOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getThuCloseTime(), formatter);
        } else if (todayDayOfWeek == FRI.getDayOfWeek()) {
            if (store.getFriOpenTime() == null || store.getFriCloseTime() == null)
                return null; // 오늘이 금요일인데 금요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getFriOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getFriCloseTime(), formatter);
        } else if (todayDayOfWeek == SAT.getDayOfWeek()) {
            if (store.getSatOpenTime() == null || store.getSatCloseTime() == null)
                return null; // 오늘이 토요일인데 토요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getSatOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getSatCloseTime(), formatter);
        } else {
            if (store.getSunOpenTime() == null || store.getSunCloseTime() == null)
                return null; // 오늘이 일요일인데 일요일 영업시간이 설정되지 않았을 경우
            openTime = LocalTime.parse(store.getSunOpenTime(), formatter);
            closeTime = LocalTime.parse(store.getSunCloseTime(), formatter);
        }

        LocalTime breakStartTime;
        LocalTime breakEndTime;
        String breakTime = store.getBreakTime();

        if (breakTime != null) {
            // 브레이크 타임이 설정되었을 경우에는 함께 고려
            String[] breakTimeList = breakTime.split("~");
            breakStartTime = LocalTime.parse(breakTimeList[0], formatter);
            breakEndTime = LocalTime.parse(breakTimeList[1], formatter);
            return currentTime.isAfter(openTime)
                    && currentTime.isBefore(closeTime)
                    && (currentTime.isBefore(breakStartTime) || currentTime.isAfter(breakEndTime));
        } else {
            // 브레이크 타임이 없을 경우에는 운영시간만 고려
            return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
        }
    }

    public Page<Store> findAllByNameAndState(String storeName, Pageable pageable) {
        try {
            for (Sort.Order order : pageable.getSort()) {
                String sortField = order.getProperty();
                Store.class.getDeclaredField(sortField);
            }

            List<Store> nameList =
                    storeRepository.findAllByStoreNameAndStateOrderByIdAsc(storeName, ACTIVE);
            List<Store> containNameList =
                    storeRepository.findAllByStoreNameContainingIgnoreCaseAndStateOrderByIdAsc(
                            storeName, ACTIVE);
            nameList.addAll(containNameList);

            List<Store> nameDistinctList =
                    nameList.stream().distinct().collect(Collectors.toList());

            // make nameList to page
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), nameDistinctList.size());
            return new PageImpl<>(
                    nameDistinctList.subList(start, end), pageable, nameDistinctList.size());

        } catch (NoSuchFieldException e) {
            throw new BaseException(STORE_SORT_FIELD_NOT_FOUND);
        }
    }

    @Transactional
    public void updateIsClosedToday(StoreIsClosedTodayRequest request, Long storeId) {
        Store store =
                storeRepository
                        .findByIdAndState(storeId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));
        store.updateIsClosedToday(request.isClosedToday());
    }

    public LoadSeatStatisticsInformationOfStoreResponse loadSeatStatisticsInformationOfStore(
            Long storeId) {
        Store storeFound = findByIdAndState(storeId);

        int totalNumberOfSeats = getTotalNumberOfSeatsOfStore(storeId);
        int numberOfSeatsInUse = getNumberOfSeatsInUse(storeFound);
        int numberOfRemainingSeats = totalNumberOfSeats - numberOfSeatsInUse;

        long totalNumberOfPeopleUsingStore = storeFound.getTotalNumberOfPeopleUsingStore();
        return LoadSeatStatisticsInformationOfStoreResponse.builder()
                .totalNumberOfSeats(totalNumberOfSeats)
                .numberOfRemainingSeats(numberOfRemainingSeats)
                .averageSeatUsageMinute(
                        totalNumberOfPeopleUsingStore == 0
                                ? 0
                                : (int)
                                        (storeFound.getTotalSeatUsageMinute()
                                                / totalNumberOfPeopleUsingStore))
                .build();
    }

    public int getTotalNumberOfSeatsOfStore(Long storeId) {
        int totalNumberOfSeats = 0;
        Store storeFound = findByIdAndState(storeId);

        List<StoreChair> storeChairs = storeChairService.findAllByStoreAndState(storeFound);
        totalNumberOfSeats += storeChairs.size();

        return totalNumberOfSeats;
    }

    public int getNumberOfSeatsInUse(Store store) {
        int numberOfSeatsInUse = 0;

        List<Utilization> utilizations =
                utilizationService.findAllByStoreAndUtilizationStatusAndState(
                        store, UtilizationStatus.CHECK_IN); // Todo : Static vs NonStatic

        for (Utilization utilization : utilizations) {
            switch (utilization.getUtilizationUnit()) {
                case CHAIR:
                    numberOfSeatsInUse++;
                    break;
                case SPACE:
                    List<StoreChair> storeChairs =
                            storeChairService.findAllByStoreSpaceAndState(
                                    utilization.getStoreSpace());
                    numberOfSeatsInUse += storeChairs.size();
            }
        }
        return numberOfSeatsInUse;
    }
}
