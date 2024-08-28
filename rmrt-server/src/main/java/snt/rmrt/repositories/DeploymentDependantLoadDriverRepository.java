package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;

import java.util.List;

@Repository
public interface DeploymentDependantLoadDriverRepository extends JpaRepository<DeploymentDependantLoadDriver, String> {

    @Query("select u.name from DeploymentDependantLoadDriver u")
    List<String> findAllIds();
}
