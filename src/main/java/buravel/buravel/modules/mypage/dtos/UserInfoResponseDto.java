package buravel.buravel.modules.mypage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String username;
    private String nickname;

    @Lob
    private String profileImage;
    @Email
    private String email;
    private boolean emailVerified;
}
