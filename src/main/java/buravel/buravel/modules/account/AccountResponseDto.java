package buravel.buravel.modules.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDto {
    private Long id;
    private String username;
    @Lob
    private String profileImage;
    @Email
    private String email;
    private boolean emailVerified;
}
