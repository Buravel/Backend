package buravel.buravel.modules.bookmark.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class BookmarkDto {
    @NotBlank
    @Length(min = 1,max = 30)
    private String bookmarkTitle;
}
