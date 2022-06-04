package project.Codelivery.domain.MatchResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, String> {
    MatchResult findByMatchId(int matchId);

    Boolean existsByMatchId(int matchId);

    @Query(value="SELECT match_id FROM Match_result WHERE state=0 AND((timeStamp < DATE_ADD(now(), INTERVAL -1 MINUTE) AND (user1_result=0 OR user2_result=0)) OR (user1_result=2 OR user2_result=2))", nativeQuery = true)
    List<String> findFailedMatchId();

    @Query(value="SELECT match_id FROM Match_result WHERE state=0 AND (user1_result=1 AND user2_result=1)", nativeQuery = true)
    List<String> findSuccessMatchId();
}
