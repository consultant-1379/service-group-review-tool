package snt.rmrt.services.confluence;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import snt.rmrt.models.readyReckoner.fsRelease.FSRelease;
import snt.rmrt.repositories.FSReleaseRepository;

import java.io.IOException;

@Component
public class LatestReleaseUpdater {
    private final FSReleaseRepository fsReleaseRepository;
    private final ConfluenceLogin confluenceLogin;

    @Value("${confluence.latestRelease.page}")
    private String destinationPage;


    @Autowired
    public LatestReleaseUpdater(FSReleaseRepository fsReleaseRepository,
                                ConfluenceLogin cl) {
        this.fsReleaseRepository = fsReleaseRepository;
        this.confluenceLogin = cl;
    }

    //every 24 hours (86400000 ms)
    @Scheduled(fixedRate = 86400000)
    private void updateDatabase() {
        try {
            updateLatestRelease();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void updateLatestRelease() throws IOException {
        Document releasePage = this.confluenceLogin.loginToConfluence(destinationPage);

        Element headerBeforeTable = releasePage.body().getElementById("EricssonNetworkManager(ENM)-RevisionHistory");

        Element table = headerBeforeTable.nextElementSiblings().select("table").get(0);

        Elements tableRows = table.getElementsByTag("tr");

        //last row of the table contains the latest approved release
        Element lastRow = tableRows.last();

        //column containing json
        Element lastColumn = lastRow.children().last();

        //deserialize the json object
        ObjectMapper objectMapper = new ObjectMapper();

        FSRelease fsRelease = objectMapper.readValue(lastColumn.text(), FSRelease.class);


        //only store the latest release value in the db
        long count = fsReleaseRepository.count();

        if(count > 0) {
            fsReleaseRepository.deleteAll();
        }

        fsReleaseRepository.save(fsRelease);
    }
}
