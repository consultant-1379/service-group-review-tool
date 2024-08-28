package snt.rmrt.rest.rmrt;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.GenericLoadDriver;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadDriver;
import snt.rmrt.repositories.ChangeRequestRepository;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.GenericLoadDriverRepository;
import snt.rmrt.services.gerrirt.RepositoryUpdater;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/newLoadDrivers")
public class NewLoadDriversApi {

    final private RepositoryUpdater repositoryUpdater;
    final private ChangeRequestRepository changeRequestRepository;
    final private GenericLoadDriverRepository genericLoadDriverRepository;
    final private DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    public NewLoadDriversApi(RepositoryUpdater repositoryUpdater, ChangeRequestRepository changeRequestRepository,
                             GenericLoadDriverRepository genericLoadDriverRepository,
                             DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.repositoryUpdater = repositoryUpdater;
        this.changeRequestRepository = changeRequestRepository;
        this.genericLoadDriverRepository = genericLoadDriverRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    @PostMapping
    public void saveNewLoadDrivers(@RequestBody List<NewLoadDriverHolder> newLoadDrivers) {

        newLoadDrivers.forEach(newLoadDriver -> {

            if(newLoadDriver.isGeneric()) {

                GenericLoadDriver genericLoadDriver = new GenericLoadDriver();
                genericLoadDriver.setName(newLoadDriver.getName());
                genericLoadDriver.setDescription(newLoadDriver.getDescription());
                genericLoadDriver.setValue(newLoadDriver.getValue());

                genericLoadDriverRepository.save(genericLoadDriver);

            } else {

                DeploymentDependantLoadDriver deploymentDependantLoadDriver = new DeploymentDependantLoadDriver();
                deploymentDependantLoadDriver.setName(newLoadDriver.getName());
                deploymentDependantLoadDriver.setDescription(newLoadDriver.getDescription());
                deploymentDependantLoadDriver.setValues(newLoadDriver.getValues());

                deploymentDependantLoadDriverRepository.save(deploymentDependantLoadDriver);

            }
        });


        newLoadDrivers.stream().findAny().ifPresent(newLd ->{
            changeRequestRepository.findById(newLd.getOwningElement())
                    .ifPresent(changeRequest -> {
                        repositoryUpdater.updateRepository(changeRequest.getRepository());
                    });
        });

    }
}

@Data
class NewLoadDriverHolder extends LoadDriver {
    private boolean generic;
    private Double value;
    private Map<String, String> values;
}
