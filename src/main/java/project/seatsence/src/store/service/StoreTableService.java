package project.seatsence.src.store.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.seatsence.global.entity.BaseTimeAndStateEntity.State;
import project.seatsence.src.store.dao.StoreTableRepository;
import project.seatsence.src.store.domain.StoreSpace;
import project.seatsence.src.store.domain.StoreTable;

@Service
@RequiredArgsConstructor
public class StoreTableService {

    private final StoreTableRepository storeTableRepository;

    public void saveAll(List<StoreTable> storeTableList) {
        storeTableRepository.saveAll(storeTableList);
    }

    public List<StoreTable> findAllByStoreSpaceAndState(StoreSpace storeSpace) {
        return storeTableRepository.findAllByStoreSpaceAndState(storeSpace, State.ACTIVE);
    }

    @Transactional
    public void deleteAll(List<StoreTable> storeTableList) {
        storeTableRepository.deleteAll(storeTableList);
    }
}
