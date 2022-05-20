package project.Codelivery.domain.MatchResult;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Match_result")
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private int matchId;

    @Column
    private int user1;
    private int user2;

    @Builder
    public MatchResult(int user1, int user2){
        this.user1 = user1;
        this.user2 = user2;
    }
}
