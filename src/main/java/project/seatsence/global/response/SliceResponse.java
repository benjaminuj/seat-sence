package project.seatsence.global.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
@AllArgsConstructor
public final class SliceResponse<T> {
    private final List<T> content;
    private final long page;
    private final int size;
    private boolean hasNext;

    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getNumber() + 1,
                slice.getNumberOfElements(),
                slice.hasNext());
    }

    public static <T> SliceResponse<T> of(List<T> content, Long page, int size, boolean hasNext) {
        return new SliceResponse<>(content, page + 1, size, hasNext);
    }
}
