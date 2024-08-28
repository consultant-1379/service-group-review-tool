package snt.rmrt.rest.rmrt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/deploymentDependantLoadDrivers")
public class DeploymentDependantLoadDriverApi {

    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    @Autowired
    public DeploymentDependantLoadDriverApi(DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    @GetMapping
    public List<DeploymentDependantLoadDriver> getDeploymentDependantLoadDrivers() {
        return deploymentDependantLoadDriverRepository.findAll().stream()
                .sorted(Comparator.comparing(DeploymentDependantLoadDriver::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @PostMapping
    public DeploymentDependantLoadDriver createDeploymentDependantLoadDriver(@RequestBody DeploymentDependantLoadDriver deploymentDependantLoadDriver) {
        return deploymentDependantLoadDriverRepository.save(deploymentDependantLoadDriver);
    }

    @PutMapping
    public DeploymentDependantLoadDriver updateDeploymentDependantLoadDriver(@RequestBody DeploymentDependantLoadDriver deploymentDependantLoadDriver) {
        return deploymentDependantLoadDriverRepository.save(deploymentDependantLoadDriver);
    }

    @DeleteMapping
    public void getDeploymentDependantLoadDriver(@RequestParam String name) {
        deploymentDependantLoadDriverRepository.deleteById(name);
    }

}
