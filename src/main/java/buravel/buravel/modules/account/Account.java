package buravel.buravel.modules.account;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@EqualsAndHashCode(of = "id")
public class Account {
    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private String username; // 유저id
    private String nickname; //유저닉네임

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

    @JsonIgnore
    private String emailCheckToken;

    //private LocalDateTime emailCheckTokenGeneratedAt;

    public void generateEmailCheckToken() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid.substring(0, 10);
        this.emailCheckToken = uuid;
        //this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public void completeSignUp() {
        this.setEmailVerified(true);
    }
}
