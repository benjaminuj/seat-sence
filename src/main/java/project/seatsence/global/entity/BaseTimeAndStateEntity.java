package project.seatsence.global.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeAndStateEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp private LocalDateTime updatedAt;

    //    @Convert(converter = StateAttributeConverter.class)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    protected State state = State.ACTIVE;

    public enum State {
        ACTIVE,
        INACTIVE;
    }
}
