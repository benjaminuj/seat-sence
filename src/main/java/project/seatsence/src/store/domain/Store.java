package project.seatsence.src.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import project.seatsence.global.entity.BaseEntity;
import project.seatsence.global.utils.EnumUtils;
import project.seatsence.src.store.dto.request.admin.basic.StoreBasicInformationRequest;
import project.seatsence.src.store.dto.request.admin.basic.StoreOperatingTimeRequest;
import project.seatsence.src.user.domain.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String businessRegistrationNumber;

    @Column(nullable = false)
    private LocalDate openDate;

    @Column(nullable = false)
    private String adminName;

    @NotBlank private String storeName;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private String address;
    private String detailAddress;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String telNum;

    private String dayOff;
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
    private String breakTime;
    private String useTimeLimit;

    @ColumnDefault("false")
    private boolean isClosedToday;

    @JsonIgnore
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreMember> memberList = new ArrayList<>();

    @ColumnDefault("0")
    private long totalSeatUsageMinute; // 해당 가게 이용 분 합

    @ColumnDefault("0")
    private long totalNumberOfPeopleUsingStore; // 해당 가게 이용 인원 합

    @Builder
    public Store(
            User user,
            String businessRegistrationNumber,
            LocalDate openDate,
            String adminName,
            String storeName,
            String address,
            String detailAddress) {
        this.user = user;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.openDate = openDate;
        this.adminName = adminName;
        this.storeName = storeName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.isClosedToday = false;
    }

    public void updateBasicInformation(StoreBasicInformationRequest request) {
        this.storeName = request.getStoreName();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.category = EnumUtils.getEnumFromString(request.getCategory(), Category.class);
        this.introduction = request.getIntroduction();
        this.telNum = request.getTelNum();
    }

    public void updateOperatingTime(StoreOperatingTimeRequest request) {
        this.dayOff = EnumUtils.getStringFromEnumList(request.getDayOff());
        this.monOpenTime = request.getMonOpenTime();
        this.monCloseTime = request.getMonCloseTime();
        this.tueOpenTime = request.getTueOpenTime();
        this.tueCloseTime = request.getTueCloseTime();
        this.wedOpenTime = request.getWedOpenTime();
        this.wedCloseTime = request.getWedCloseTime();
        this.thuOpenTime = request.getThuOpenTime();
        this.thuCloseTime = request.getThuCloseTime();
        this.friOpenTime = request.getFriOpenTime();
        this.friCloseTime = request.getFriCloseTime();
        this.satOpenTime = request.getSatOpenTime();
        this.satCloseTime = request.getSatCloseTime();
        this.sunOpenTime = request.getSunOpenTime();
        this.sunCloseTime = request.getSunCloseTime();
        this.breakTime = request.getBreakTime();
        this.useTimeLimit = request.getUseTimeLimit();
    }

    public void delete() {
        this.state = State.INACTIVE;
    }

    public void updateIsClosedToday(boolean isClosedToday) {
        this.isClosedToday = isClosedToday;
    }

    public void updateImages(String images) {
        this.images = images;
    }

    public void updateTotalSeatUsageTime(long totalSeatUsageMinute) {
        this.totalSeatUsageMinute = totalSeatUsageMinute;
    }

    public void updateTotalNumberOfPeopleUsingStore(long totalNumberOfPeopleUsingStore) {
        this.totalNumberOfPeopleUsingStore = totalNumberOfPeopleUsingStore;
    }
}
