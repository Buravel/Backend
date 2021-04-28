package buravel.buravel.modules.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    @Email
    @Column(unique = true,nullable = false)
    private String email;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }
}
