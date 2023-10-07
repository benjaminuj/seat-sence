package project.seatsence.src.store.dto.response.user;

import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StoreListResponse {

    private int curCount;
    private int curPage;
    private Long totalCount;
    private int totalPage;
    private List<StoreResponse> storeResponseList;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class StoreResponse {

        private Long id;
        private String name;
        private String introduction;
        private String address;
        private String detailAddress;
        private String mainImage;
        private Boolean isOpen;
        private String telNum;
    }
}
