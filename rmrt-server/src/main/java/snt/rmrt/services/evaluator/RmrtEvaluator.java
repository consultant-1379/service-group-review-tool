package snt.rmrt.services.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.ReferenceDeploymentRepository;

import java.util.List;

@Service
@Slf4j
public class RmrtEvaluator extends Evaluator {

    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;
    private final ReferenceDeploymentRepository referenceDeploymentRepository;

    public RmrtEvaluator(DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository,
                         ReferenceDeploymentRepository referenceDeploymentRepository) {
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
        this.referenceDeploymentRepository = referenceDeploymentRepository;
    }


    public void evaluateAllResourceModels(List<Repository> allRepositories) {

        List<ReferenceDeployment> referenceDeployments = referenceDeploymentRepository.findAll();
        List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers = deploymentDependantLoadDriverRepository.findAll();

        Workbook workbook = new XSSFWorkbook();

        allRepositories.forEach(repository -> {
            evaluateResourceModel(workbook, referenceDeployments, deploymentDependantLoadDrivers, repository.getResourceModel());
            repository.getChangeRequests().forEach(changeRequest -> {
                evaluateResourceModel(workbook, referenceDeployments, deploymentDependantLoadDrivers, changeRequest.getResourceModel());
            });
        });
    }
    public void evaluateRepository(Repository repository) {
        List<ReferenceDeployment> referenceDeployments = referenceDeploymentRepository.findAll();
        List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers = deploymentDependantLoadDriverRepository.findAll();

        Workbook workbook = new XSSFWorkbook();

        evaluateResourceModel(workbook, referenceDeployments, deploymentDependantLoadDrivers, repository.getResourceModel());
        repository.getChangeRequests().forEach(changeRequest ->
                evaluateResourceModel(workbook, referenceDeployments, deploymentDependantLoadDrivers, changeRequest.getResourceModel())
        );
    }

}
