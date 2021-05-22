package buravel.buravel.modules.bookmarkPost;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckResponseDto {
    private Long planId;
    private List<BookmarkPostResponseDto> bookmarkPostResponseDtoList;
}
