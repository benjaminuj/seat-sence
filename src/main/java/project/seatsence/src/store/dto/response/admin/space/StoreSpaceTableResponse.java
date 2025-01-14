package project.seatsence.src.store.dto.response.admin.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreSpaceTableResponse {

    @JsonProperty("i")
    private Long id; // store table primary key

    @JsonProperty("w")
    private int width;

    @JsonProperty("h")
    private int height;

    @JsonProperty("x")
    private int tableX;

    @JsonProperty("y")
    private int tableY;
}
