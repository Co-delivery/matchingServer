package project.Codelivery.domain.MatchResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchResultRepository extends JpaRepository<MatchResult, String> {
    MatchResult findByMatchId(int matchId);

    Boolean existsByMatchId(int matchId);

    @Query(value="SELECT match_id FROM Match_result WHERE (state=2 AND (timeStamp < DATE_ADD(now(), INTERVAL -1 MINUTE)) AND (user1_pay_result=0 OR user2_pay_result=0)) OR (state=0 AND((timeStamp < DATE_ADD(now(), INTERVAL -1 MINUTE) AND (user1_result=0 OR user2_result=0)) OR (user1_result=2 OR user2_result=2)))", nativeQuery = true)
    List<String> findFailedMatchId();

    @Query(value="SELECT match_id FROM Match_result WHERE state=0 AND (user1_result=1 AND user2_result=1)", nativeQuery = true)
    List<String> findSuccessMatchId();

    @Query(value="SELECT * FROM Match_result WHERE (state=0 AND ((user1_result=0 AND user2_result=0) OR (user1_result=0 AND user2_result=1) OR (user1_result=1 AND user2_result=0))) OR (state=2 AND (user1_pay_result=0 OR user2_pay_result=0))", nativeQuery = true)
    List<MatchResult> findNeedAlarmMatchId();

    @Query(value="SELECT match_id FROM Match_result WHERE state=2 AND user1_pay_result=1 AND user2_pay_result=1", nativeQuery = true)
    List<String> findCompleteMatchId();
}
