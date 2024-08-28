package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentType;

@Repository
public interface DeploymentTypeRepository extends JpaRepository<DeploymentType, String> {
}
