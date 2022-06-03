package project.Codelivery;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import project.Codelivery.service.MatchService;


@Component
public class RunAfterApplicationStart implements ApplicationRunner {
    private final MatchService matchService;

    public RunAfterApplicationStart(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("*************Matching Service Start*************");
        //while(true) {
               matchService.matching();
               matchService.matchResultCheck();
        //}
    }
}
