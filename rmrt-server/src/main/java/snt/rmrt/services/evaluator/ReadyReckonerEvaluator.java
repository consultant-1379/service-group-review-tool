package snt.rmrt.services.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.RepositoryRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReadyReckonerEvaluator extends Evaluator {

    @Value("${enmNetworkSize.project}")
    private String enmNetworkSizeProject;

    private final RepositoryRepository repositoryRepository;
    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    public ReadyReckonerEvaluator(RepositoryRepository repositoryRepository, DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.repositoryRepository = repositoryRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    public List<Repository> evaluateAllResourceModelsForReadyReckoner(List<ReferenceDeployment> readyReckonerForms, Map<String, Integer> dataFromForm) {

        List<Repository> repositories = repositoryRepository.findAll().stream()
                .filter(repository -> repository.getResourceModel() != null)
                .collect(Collectors.toList());

        List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers = deploymentDependantLoadDriverRepository.findAll();
        dataFromForm.forEach((name, val) -> {
            deploymentDependantLoadDrivers.stream()
                    .filter(depLd->depLd.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .ifPresent(depLd ->
                            readyReckonerForms.forEach(ref->
                                    depLd.getValues().put(ref.getName(), val.toString())
                            )
                    );
        });


        // Collect only those that are required for evaluation.
        List<Repository> repositoriesToEvaluate = repositories.stream()
                .filter(repository -> repository.getMediationComponent()
                        || !repository.getKeyDimensioningValues().isEmpty()
                        || repository.getProject().equals(enmNetworkSizeProject))
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();
        repositoriesToEvaluate.forEach(repository -> evaluateResourceModel(workbook, readyReckonerForms, deploymentDependantLoadDrivers, repository.getResourceModel()));

        return repositoriesToEvaluate;
    }

}
