package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;

@Repository
public interface KeyDimensioningValuesRepository extends JpaRepository<KeyDimensioningValue, String> {
}