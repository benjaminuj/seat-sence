package project.seatsence.src.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignInResponse {
    private String accessToken;
    private Long storeId;
    private String storeName;
    private String mainImage;
    private String introduction;
    private String permissionByMenu;
}
