package project.Codelivery.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "User")
public class User {
    @Id @Column(name = "Id")
    private String userId;

    @Column
    private String password;
    private String address;
    private String token;
    private String nickname;

    @Builder
    public User (String userId, String password, String address, String token, String nickname) {
        this.userId = userId;
        this.password = password;
        this.address = address;
        this.token = token;
        this.nickname = nickname;
    }
}