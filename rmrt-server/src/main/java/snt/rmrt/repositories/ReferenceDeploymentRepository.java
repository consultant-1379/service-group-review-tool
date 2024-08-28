package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;

@Repository
public interface ReferenceDeploymentRepository extends JpaRepository<ReferenceDeployment, String> {

}
