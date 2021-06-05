package buravel.buravel.modules.bookmarkPost.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequestDto {

    @NotNull
    private Long planId;

    private Long[] bookmarkPostIdList;
}
