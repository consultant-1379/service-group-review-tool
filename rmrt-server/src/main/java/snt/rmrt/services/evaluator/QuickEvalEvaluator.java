package snt.rmrt.services.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentType;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;
import snt.rmrt.models.rmrt.repository.resourceModel.Property;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.LoadConversionFormulaeRepository;
import snt.rmrt.repositories.ParametersRepository;

import java.util.*;

@Service
@Slf4j
public class QuickEvalEvaluator extends Evaluator {

    private final LoadConversionFormulaeRepository loadConversionFormulaeRepository;
    private final ParametersRepository parametersRepository;
    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    public QuickEvalEvaluator(LoadConversionFormulaeRepository loadConversionFormulaeRepository,
                              ParametersRepository parametersRepository, DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.loadConversionFormulaeRepository = loadConversionFormulaeRepository;
        this.parametersRepository = parametersRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    public Map<String, Double> quickEval(List<String> outputs, Map<String, Integer> inputs) {

        List<LoadConversionFormula> loadConversionFormulae = new ArrayList<>();
        List<Property> properties = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        outputs.forEach(out -> loadConversionFormulaeRepository.findByNameAndIsMaster(out, true)
                .ifPresent(found -> {
                    loadConversionFormulae.add(found);
                    repositories
                            .add(found.getDeploymentDependency().getResourceModel().getRepository());
                }));
        outputs.forEach(out -> parametersRepository
                .findByNameAndIsMaster(out, true)
                .ifPresent(properties::add));


        Workbook workbook = new XSSFWorkbook();
        ReferenceDeployment referenceDeployment = new ReferenceDeployment();
        final String name = "QuickEval";
        referenceDeployment.setName(name);
        DeploymentType deploymentType = new DeploymentType();
        deploymentType.setSizeKey("not relevant");
        referenceDeployment.setDeploymentType(deploymentType);

        List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers = deploymentDependantLoadDriverRepository.findAll();
            inputs.forEach((inputName, val) ->
                deploymentDependantLoadDrivers.stream()
                        .filter(depLd-> depLd.getName().equalsIgnoreCase(inputName))
                        .findFirst()
                        .ifPresent(depLd ->
                            depLd.getValues().put(referenceDeployment.getName(), val.toString())
                        )
            );

        repositories.forEach(repository -> evaluateResourceModel(workbook, Arrays.asList(referenceDeployment), deploymentDependantLoadDrivers, repository.getResourceModel()));

        Map<String, Double> results = new HashMap<>();
        properties.forEach(prop ->
                results.put(prop.getName(), prop.getDefaultValue())
        );
        loadConversionFormulae.forEach(ldc ->
            results.put(ldc.getName(), ldc.getValues().get(name))
        );


        return results;
    }

}
