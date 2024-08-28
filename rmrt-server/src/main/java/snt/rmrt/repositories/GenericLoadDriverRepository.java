package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.referenceDeployment.GenericLoadDriver;

import java.util.List;

@Repository
public interface GenericLoadDriverRepository extends JpaRepository<GenericLoadDriver, String> {
    @Query("select u.name from GenericLoadDriver u")
    List<String> findAllIds();
}
