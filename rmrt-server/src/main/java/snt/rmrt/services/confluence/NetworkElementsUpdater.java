package snt.rmrt.services.confluence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import snt.rmrt.models.readyReckoner.networkElement.NetworkElement;
//import snt.rmrt.repositories.NetworkElementInputRepository;
import snt.rmrt.repositories.NetworkElementsRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NetworkElementsUpdater {

    private final NetworkElementsRepository networkElementsRepository;
//    private final NetworkElementInputRepository networkInputRepository;
    private final ConfluenceLogin confluenceLogin;

    @Value("${confluence.networkElements.page}")
    private String destinationPage;


    @Autowired
    public NetworkElementsUpdater(NetworkElementsRepository networkElementsRepository,
                                  ConfluenceLogin cl
                                  ) {
        this.networkElementsRepository = networkElementsRepository;
        this.confluenceLogin = cl;
    }

    //every 24 hours (86400000 s)
    @Scheduled(fixedRate = 86400000)
    private void updateDatabase() {
        try {
            log.info("Updating network elements");
            updateNetworkElements();
            log.info("Finished network elements update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateNetworkElements() throws IOException {

        //login to confluence and get response page
        Document responseDoc = confluenceLogin.loginToConfluence(destinationPage);

        //heading before table
        Element heading = responseDoc.getElementById("SupportedNetworkSize-UseCaseOverview");

        // Search Element after title
        Element table = null;

        for (Element nextElement : heading.nextElementSiblings()) {
            if (nextElement.is("div.table-wrap")) {
                table = nextElement;
                break;
            }
        }


        if (table != null) {
            //get <tr> tags
            List<Element> htmlRows = table.selectFirst("tbody").children();


            List<NetworkElement> oldNetworkElements = networkElementsRepository.findAll();
            List<NetworkElement> newNetworkElements = new ArrayList<>();

            //first row is the heading ; start from second row
            for (int i = 1; i < htmlRows.size(); ++i) {

                Element currentRow = htmlRows.get(i);

                //check if there is a release value
                if ((currentRow.child(4).wholeText().contains("Release"))
                        &&
                        //********to be removed when all data is json **********)
                        (currentRow.child(4).wholeText().contains("{"))
                    //********************************************************
                ) {

                    try {
                        String columnJsonValue = currentRow.child(4).wholeText();
                        NetworkElement networkElement
                                = new ObjectMapper().readValue(columnJsonValue, NetworkElement.class);
                        newNetworkElements.add(networkElement);

                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }

            try {

                newNetworkElements.forEach(newNetworkElement -> {
                    try {
                        networkElementsRepository.save(newNetworkElement);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                });
                oldNetworkElements.removeAll(newNetworkElements);
                networkElementsRepository.deleteAll(oldNetworkElements);

            } catch (Exception e) {
                log.error(e.getMessage());
            }


        }

    }
}
