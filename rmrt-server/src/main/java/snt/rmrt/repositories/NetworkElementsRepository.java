package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import snt.rmrt.models.readyReckoner.networkElement.NetworkElement;

import java.util.List;

public interface NetworkElementsRepository extends JpaRepository<NetworkElement, String> {

    List<NetworkElement> findByReleaseLessThanEqual(Double fsRelease);

    List<Object> findByDomainAndReleaseLessThanEqual(String domain, Double fsRelease);

}
