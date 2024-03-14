package project.seatsence.src.store.service;

import static project.seatsence.global.code.ResponseCode.STORE_NOT_FOUND;
import static project.seatsence.global.code.ResponseCode.STORE_SPACE_NOT_FOUND;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.dao.StoreSpaceRepository;
import project.seatsence.src.store.domain.*;
import project.seatsence.src.store.dto.request.admin.basic.StoreReservationUnitRequest;
import project.seatsence.src.store.dto.request.admin.space.StoreSpaceChairRequest;
import project.seatsence.src.store.dto.request.admin.space.StoreSpaceCreateRequest;
import project.seatsence.src.store.dto.request.admin.space.StoreSpaceTableRequest;
import project.seatsence.src.store.dto.request.admin.space.StoreSpaceUpdateRequest;
import project.seatsence.src.store.dto.response.AllStoreSpaceTableChairResponse;
import project.seatsence.src.store.dto.response.admin.basic.StoreReservationUnitResponse;
import project.seatsence.src.store.dto.response.admin.space.StoreSpaceChairResponse;
import project.seatsence.src.store.dto.response.admin.space.StoreSpaceCreateResponse;
import project.seatsence.src.store.dto.response.admin.space.StoreSpaceSeatResponse;
import project.seatsence.src.store.dto.response.admin.space.StoreSpaceTableResponse;
import project.seatsence.src.store.mapper.StoreChairMapper;
import project.seatsence.src.store.mapper.StoreTableMapper;

@Service
@RequiredArgsConstructor
public class StoreSpaceService {

    private final StoreSpaceRepository storeSpaceRepository;
    private final StoreTableService storeTableService;
    private final StoreChairService storeChairService;
    private final StoreService storeService;

    @Transactional
    public StoreSpaceCreateResponse save(Long id, StoreSpaceCreateRequest storeSpaceCreateRequest) {
        Store store = storeService.findByIdAndState(id);
        List<StoreTable> storeTableList = new ArrayList<>();
        List<StoreChair> storeChairList = new ArrayList<>();

        StoreSpace storeSpace =
                StoreSpace.builder()
                        .store(store)
                        .name(storeSpaceCreateRequest.getStoreSpaceName())
                        .height(storeSpaceCreateRequest.getHeight())
                        .reservationUnit(
                                getReservationUnitFromRequest(
                                        storeSpaceCreateRequest.getReservationUnit()))
                        .storeTableList(new ArrayList<>())
                        .storeChairList(new ArrayList<>())
                        .build();

        List<StoreSpaceTableRequest> tableList = storeSpaceCreateRequest.getTableList();
        for (StoreSpaceTableRequest tableRequest : tableList) {
            StoreTable storeTable =
                    StoreTable.builder()
                            .tableX(tableRequest.getTableX())
                            .tableY(tableRequest.getTableY())
                            .width(tableRequest.getWidth())
                            .height(tableRequest.getHeight())
                            .storeSpace(storeSpace)
                            .store(store)
                            .idByWeb(tableRequest.getIdByWeb())
                            .build();
            storeTableList.add(storeTable);
            storeSpace.getStoreTableList().add(storeTable);
        }

        List<StoreSpaceChairRequest> chairList = storeSpaceCreateRequest.getChairList();

        for (StoreSpaceChairRequest chairRequest : chairList) {
            StoreChair storeChair =
                    StoreChair.builder()
                            .chairX(chairRequest.getChairX())
                            .chairY(chairRequest.getChairY())
                            .storeSpace(storeSpace)
                            .store(store)
                            .idByWeb(chairRequest.getIdByWeb())
                            .manageId(chairRequest.getManageId())
                            .build();
            storeChairList.add(storeChair);
            storeSpace.getStoreChairList().add(storeChair);
        }

        storeTableService.saveAll(storeTableList);
        storeChairService.saveAll(storeChairList);
        StoreSpace save = storeSpaceRepository.save(storeSpace);
        return new StoreSpaceCreateResponse(save.getId());
    }

    @Cacheable(value = "tablesAndChairsPerSpace")
    public StoreSpaceSeatResponse getStoreSpaceSeat(Long storeSpaceId) {
        StoreSpace storeSpace =
                storeSpaceRepository
                        .findByIdAndStateWithTables(storeSpaceId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_NOT_FOUND));

        List<StoreSpaceTableResponse> tableResponses =
                storeSpace.getStoreTableList().stream()
                        .map(
                                storeTable ->
                                        StoreTableMapper.convertToStoreSpaceTableResponse(
                                                storeTable))
                        .collect(Collectors.toList());

        List<StoreChair> storeChairList = storeChairService.findAllByStoreSpaceAndState(storeSpace);
        List<StoreSpaceChairResponse> chairResponses =
                storeChairList.stream()
                        .map(
                                storeChair ->
                                        StoreChairMapper.convertToStoreSpaceChairResponse(
                                                storeChair))
                        .collect(Collectors.toList());

        StoreSpaceSeatResponse storeSpaceSeatResponse =
                StoreSpaceSeatResponse.builder()
                        .storeSpaceId(storeSpace.getId())
                        .storeSpaceName(storeSpace.getName())
                        .height(storeSpace.getHeight())
                        .tableList(tableResponses)
                        .chairList(chairResponses)
                        .reservationUnit(
                                getReservationUnitFromEntity(storeSpace.getReservationUnit()))
                        .build();

        return storeSpaceSeatResponse;
    }

    public StoreSpace findByIdAndState(Long id) {
        return storeSpaceRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(STORE_SPACE_NOT_FOUND));
    }

    public List<StoreSpace> findAllByStoreAndState(Long storeId) {
        Store store = storeService.findByIdAndState(storeId);
        return storeSpaceRepository.findAllByStoreAndState(store, ACTIVE);
    }

    @Transactional
    public void updateStoreSpace(
            Long storeSpaceId, StoreSpaceUpdateRequest storeSpaceUpdateRequest) {
        StoreSpace storeSpace =
                storeSpaceRepository
                        .findByIdAndState(storeSpaceId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_SPACE_NOT_FOUND));

        List<StoreTable> storeTableList = storeTableService.findAllByStoreSpaceAndState(storeSpace);
        storeTableService.deleteAll(storeTableList); // 기존에 등록되어있던 테이블 전체 삭제

        List<StoreChair> storeChairList = storeChairService.findAllByStoreSpaceAndState(storeSpace);
        storeChairService.deleteAll(storeChairList); // 기존에 등록되어있던 의자 전체 삭제
        storeSpace.updateBasicInformation(storeSpaceUpdateRequest); // 이름, 예약 단위, 높이 변경

        // 테이블 및 좌석 새로 등록
        List<StoreTable> newStoreTableList = new ArrayList<>();
        List<StoreSpaceTableRequest> tableList = storeSpaceUpdateRequest.getTableList();

        for (StoreSpaceTableRequest tableRequest : tableList) {
            StoreTable storeTable =
                    StoreTable.builder()
                            .tableX(tableRequest.getTableX())
                            .tableY(tableRequest.getTableY())
                            .width(tableRequest.getWidth())
                            .height(tableRequest.getHeight())
                            .storeSpace(storeSpace)
                            .store(storeSpace.getStore())
                            .idByWeb(tableRequest.getIdByWeb())
                            .build();
            newStoreTableList.add(storeTable);
        }
        storeSpace.getStoreTableList().addAll(newStoreTableList); // store space에 테이블 추가
        storeTableService.saveAll(newStoreTableList);

        List<StoreChair> newStoreChairList = new ArrayList<>();
        List<StoreSpaceChairRequest> chairList = storeSpaceUpdateRequest.getChairList();

        for (StoreSpaceChairRequest chairRequest : chairList) {
            StoreChair storeChair =
                    StoreChair.builder()
                            .chairX(chairRequest.getChairX())
                            .chairY(chairRequest.getChairY())
                            .storeSpace(storeSpace)
                            .store(storeSpace.getStore())
                            .idByWeb(chairRequest.getIdByWeb())
                            .manageId(chairRequest.getManageId())
                            .build();
            newStoreChairList.add(storeChair);
        }
        storeSpace.getStoreChairList().addAll(newStoreChairList); // store space에 의자 추가
        storeChairService.saveAll(newStoreChairList);
    }

    public Boolean reservationUnitIsOnlySeat(StoreSpace storeSpace) {
        boolean result = false;
        if (storeSpace.getReservationUnit().equals(ReservationUnit.SEAT)) {
            result = true;
        }
        return result;
    }

    public Boolean reservationUnitIsOnlySpace(StoreSpace storeSpace) {
        boolean result = false;
        if (storeSpace.getReservationUnit().equals(ReservationUnit.SPACE)) {
            result = true;
        }
        return result;
    }

    @Transactional
    public void deleteById(Long storeSpaceId) {
        StoreSpace storeSpace =
                storeSpaceRepository
                        .findByIdAndState(storeSpaceId, ACTIVE)
                        .orElseThrow(() -> new BaseException(STORE_SPACE_NOT_FOUND));
        storeSpace.setState(INACTIVE);
    }

    private ReservationUnit getReservationUnitFromRequest(StoreReservationUnitRequest request) {
        if (request.getSpace() && !request.getChair()) return ReservationUnit.SPACE;
        else if (!request.getSpace() && request.getChair()) return ReservationUnit.SEAT;
        else return ReservationUnit.BOTH;
    }

    private StoreReservationUnitResponse getReservationUnitFromEntity(ReservationUnit entity) {
        if (entity == ReservationUnit.SPACE) return new StoreReservationUnitResponse(true, false);
        else if (entity == ReservationUnit.SEAT)
            return new StoreReservationUnitResponse(false, true);
        else return new StoreReservationUnitResponse(true, true);
    }

    public List<AllStoreSpaceTableChairResponse> getAllStoreSpaceTableChairResponse() {
        List<Store> stores = storeService.findAllStore();
        List<AllStoreSpaceTableChairResponse> result = new ArrayList<>();

        for (Store store : stores) {
            List<StoreSpace> foundSpaces = findAllByStoreAndState(store.getId());

            for (StoreSpace space : foundSpaces) {
                List<StoreTable> tables = storeTableService.findAllByStoreSpaceAndState(space);

                for (StoreTable table : tables) {
                    AllStoreSpaceTableChairResponse response =
                            new AllStoreSpaceTableChairResponse(
                                    store.getId(),
                                    store.getStoreName(),
                                    space.getId(),
                                    space.getName(),
                                    space.getHeight(),
                                    table.getId(),
                                    table.getHeight(),
                                    table.getWidth(),
                                    table.getTableX(),
                                    table.getTableY());

                    result.add(response);
                }

                List<StoreChair> chairs = storeChairService.findAllByStoreSpaceAndState(space);

                for (StoreChair chair : chairs) {
                    AllStoreSpaceTableChairResponse response =
                            new AllStoreSpaceTableChairResponse(
                                    store.getId(),
                                    store.getStoreName(),
                                    space.getId(),
                                    space.getName(),
                                    space.getHeight(),
                                    chair.getId(),
                                    chair.getChairX(),
                                    chair.getChairY());

                    result.add(response);
                }
            }
        }

        return result;
    }
}
