package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.IdClass;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;

import java.util.Optional;

@Repository
public interface LoadConversionFormulaeRepository extends JpaRepository<LoadConversionFormula, IdClass> {

    Optional<LoadConversionFormula> findByNameAndIsMaster(String name, boolean master);

}
