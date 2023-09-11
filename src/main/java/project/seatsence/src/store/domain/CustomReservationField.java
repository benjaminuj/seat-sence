package project.seatsence.src.store.domain;

import javax.persistence.*;
import lombok.*;
import project.seatsence.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomReservationField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String title;

    @Enumerated(EnumType.STRING)
    private CustomReservationFieldType type;

    private String contentGuide;

    @Builder
    public CustomReservationField(
            Store store, String title, CustomReservationFieldType type, String contentGuide) {
        this.store = store;
        this.title = title;
        this.type = type;
        this.contentGuide = contentGuide;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(CustomReservationFieldType type) {
        this.type = type;
    }

    public void setContentGuide(String contentGuide) {
        this.contentGuide = contentGuide;
    }
}