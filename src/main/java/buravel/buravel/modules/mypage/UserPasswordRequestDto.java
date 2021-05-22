package buravel.buravel.modules.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPasswordRequestDto {
    @NotBlank
    @Length(min = 8,max = 40)
    private String password;
}

