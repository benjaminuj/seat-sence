package project.seatsence.src.store.dto.response.user;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.seatsence.src.store.domain.Category;
import project.seatsence.src.store.domain.Day;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreDetailResponse {

    private Long id;
    private String storeName;
    private String address;
    private String detailAddress;
    private String introduction;

    private Category category;
    private String mainImage; // TODO 메인 이미지 및 이미지 리스트 전송
    private String telNum;
    private String monOpenTime;
    private String monCloseTime;
    private String tueOpenTime;
    private String tueCloseTime;
    private String wedOpenTime;
    private String wedCloseTime;
    private String thuOpenTime;
    private String thuCloseTime;
    private String friOpenTime;
    private String friCloseTime;
    private String satOpenTime;
    private String satCloseTime;
    private String sunOpenTime;
    private String sunCloseTime;
    private List<Day> dayOff;
}
