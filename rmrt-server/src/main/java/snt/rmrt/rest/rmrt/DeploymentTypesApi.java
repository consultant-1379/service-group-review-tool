package snt.rmrt.rest.rmrt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentType;
import snt.rmrt.repositories.DeploymentTypeRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/deploymentTypes")
public class DeploymentTypesApi {

    private final DeploymentTypeRepository deploymentTypeRepository;

    @Autowired
    public DeploymentTypesApi(DeploymentTypeRepository deploymentDependantLoadDriverRepository) {
        this.deploymentTypeRepository = deploymentDependantLoadDriverRepository;
    }

    @GetMapping
    public List<DeploymentType> getDeploymentTypes() {
        return deploymentTypeRepository.findAll().stream()
                .filter(deploymentType -> !deploymentType.getReferenceDeployments().isEmpty())
                .sorted(Comparator.comparing(DeploymentType::getOrder))
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<DeploymentType> getAllDeploymentTypes() {
        return deploymentTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(DeploymentType::getOrder))
                .collect(Collectors.toList());
    }

    @PostMapping
    public void createDeploymentType(@RequestBody DeploymentType deploymentType) {
        deploymentTypeRepository.save(deploymentType);
    }

    @PutMapping
    public void updateDeploymentType(@RequestBody DeploymentType deploymentType) {
        deploymentTypeRepository.save(deploymentType);
    }

    @DeleteMapping
    public void getDeploymentDependantLoadDriver(@RequestParam String deploymentType) {
        deploymentTypeRepository.deleteById(deploymentType);
    }
}
