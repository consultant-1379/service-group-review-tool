package snt.rmrt.rest.readyReckoner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import snt.rmrt.models.readyReckoner.fsRelease.FSRelease;
import snt.rmrt.models.readyReckoner.networkElement.NetworkElement;
import snt.rmrt.repositories.FSReleaseRepository;
import snt.rmrt.repositories.NetworkElementsRepository;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("api/NetworkElementTypes")
public class ConfluenceApi {

    private final NetworkElementsRepository networkElementsRepository;
    private final FSReleaseRepository fsReleaseRepository;

    @Autowired
    public ConfluenceApi(NetworkElementsRepository networkElementsRepository,
                         FSReleaseRepository fsReleaseRepository)  {
        this.networkElementsRepository = networkElementsRepository;
        this.fsReleaseRepository = fsReleaseRepository;
    }


    //Get supported NE Types for release value <= latest approved release
    @GetMapping("/supported")
    public Map<String, Object> getSupportedNEByDomain() {
        Map<String, Object> supportedNETypes = new HashMap<>();
        Set<String> domains = new HashSet<>();

        List<NetworkElement> networkElements =  networkElementsRepository.findAll();

        //get the different domains
        for(NetworkElement ne: networkElements) {
            domains.add(ne.getDomain());
        }

        //latest approved release -> in the database only one entry is stored
        FSRelease fsRelease = fsReleaseRepository.findAll().get(0);

        supportedNETypes.put("latestRelease", fsRelease);

        Map<String, Object> supportedByDomain = new HashMap<>();

        //get all network elements for each domain
        for (String domain : domains) {
            double latestRelease = Double.parseDouble(fsRelease.getRelease());

            List<Object> neTypesByDomain = networkElementsRepository.findByDomainAndReleaseLessThanEqual(domain, latestRelease);

            supportedByDomain.put(domain, neTypesByDomain);
        }

        supportedNETypes.put("domains", supportedByDomain);

        return supportedNETypes;
    }

    //Get supported NE Types for release value <= latest approved release
    @GetMapping
    public Map<String, Object> getNetworkElementTypes() {
        Map<String, Object> returnObject = new HashMap<>();

        //latest release
        FSRelease latestRelease = fsReleaseRepository.findAll().get(0);
        returnObject.put("latestRelease", latestRelease);

        List<NetworkElement> networkElements =  networkElementsRepository.findByReleaseLessThanEqual(Double.parseDouble(latestRelease.getRelease()));
        returnObject.put("networkElementTypes", networkElements);


        return returnObject;
    }

}
