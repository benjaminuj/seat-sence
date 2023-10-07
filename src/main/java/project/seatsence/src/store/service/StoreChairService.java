package project.seatsence.src.store.service;

import static project.seatsence.global.code.ResponseCode.*;
import static project.seatsence.global.entity.BaseTimeAndStateEntity.State.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.exceptions.BaseException;
import project.seatsence.src.store.dao.StoreChairRepository;
import project.seatsence.src.store.domain.Store;
import project.seatsence.src.store.domain.StoreChair;
import project.seatsence.src.store.domain.StoreSpace;

@Service
@RequiredArgsConstructor
public class StoreChairService {

    private final StoreChairRepository storeChairRepository;

    public void saveAll(List<StoreChair> storeChairList) {
        storeChairRepository.saveAll(storeChairList);
    }

    public List<StoreChair> findAllByStoreSpaceAndState(StoreSpace storeSpace) {
        return storeChairRepository.findAllByStoreSpaceAndState(storeSpace, ACTIVE);
    }

    public StoreChair findByIdAndState(Long id) {
        return storeChairRepository
                .findByIdAndState(id, ACTIVE)
                .orElseThrow(() -> new BaseException(STORE_CHAIR_NOT_FOUND));
    }

    public List<StoreChair> findAllByStoreAndState(Store store) {
        return storeChairRepository.findAllByStoreAndState(store, ACTIVE);
    }

    @Transactional
    public void deleteAll(List<StoreChair> storeChairList) {
        storeChairRepository.deleteAll(storeChairList);
    }
}
