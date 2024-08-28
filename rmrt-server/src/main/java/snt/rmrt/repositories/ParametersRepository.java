package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import snt.rmrt.models.rmrt.IdClass;
import snt.rmrt.models.rmrt.repository.resourceModel.Property;

import java.util.Optional;

public interface ParametersRepository extends JpaRepository<Property, IdClass>  {

    Optional<Property> findByNameAndIsMaster(String name, boolean master);

}
