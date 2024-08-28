package snt.rmrt.rest.rmrt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.ReferenceDeploymentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("api/referenceDeployments")
public class ReferenceDeploymentApi {

    private final ReferenceDeploymentRepository referenceDeploymentRepository;
    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    @Autowired
    public ReferenceDeploymentApi(ReferenceDeploymentRepository referenceDeploymentRepository, DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.referenceDeploymentRepository = referenceDeploymentRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    @GetMapping
    public List<ReferenceDeployment> getReferenceDeployments() {
        return referenceDeploymentRepository.findAll().stream()
                .sorted(Comparator.comparing(ref -> ref.getDeploymentType().getOrder()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ReferenceDeployment createReferenceDeployments(@RequestBody Holder holder) {
        deploymentDependantLoadDriverRepository.saveAll(holder.getDeploymentLoadDrivers());
        return referenceDeploymentRepository.save(holder.getReferenceDeployment());
    }

    @PutMapping
    public ReferenceDeployment updateReferenceDeployments(@RequestBody Holder holder) {
        List<DeploymentDependantLoadDriver> unmodifiedLds = deploymentDependantLoadDriverRepository.findAll();

        if (holder.isCopy()) {
            // Add new values
            holder.getDeploymentLoadDrivers().forEach(depLd -> {
                depLd.getValues().put(
                        holder.getNewName(),
                        depLd.getValues().get(holder.getReferenceDeployment().getName())
                );
                // Rewirte original values to copied Ref
                unmodifiedLds.stream()
                        .filter(unmodified -> depLd.getName().equals(unmodified.getName()))
                        .findFirst()
                        .ifPresent(unmodified ->
                                depLd.getValues().put(
                                        holder.getReferenceDeployment().getName(),
                                        unmodified.getValues().get(holder.getReferenceDeployment().getName())
                                ));
            });

            holder.getReferenceDeployment().setName(holder.getNewName());
        }


        deploymentDependantLoadDriverRepository.saveAll(holder.getDeploymentLoadDrivers());
        return referenceDeploymentRepository.save(holder.getReferenceDeployment());
    }

    @DeleteMapping
    public void getReferenceDeployments(@RequestParam String name) {
        referenceDeploymentRepository.deleteById(name);
        List<DeploymentDependantLoadDriver> deps = deploymentDependantLoadDriverRepository.findAll();
        deps.forEach(dep -> {
            dep.getValues().remove(name);
        });
        deploymentDependantLoadDriverRepository.saveAll(deps);
    }

}

@Data
class Holder {
    private ReferenceDeployment referenceDeployment;
    private List<DeploymentDependantLoadDriver> deploymentLoadDrivers;
    private boolean copy;
    private String newName;
}
