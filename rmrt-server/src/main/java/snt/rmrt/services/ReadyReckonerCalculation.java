package snt.rmrt.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.DeploymentResults;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.DisplayValue;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.Results;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentType;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;
import snt.rmrt.repositories.DeploymentTypeRepository;
import snt.rmrt.services.evaluator.ReadyReckonerEvaluator;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class ReadyReckonerCalculation {

    private final ReadyReckonerEvaluator evaluator;
    private final DeploymentTypeRepository deploymentTypeRepository;

    @Value("${enmNetworkSize.project}")
    private String enmNetworkSizeProject;

    public ReadyReckonerCalculation(ReadyReckonerEvaluator evaluator, DeploymentTypeRepository deploymentTypeRepository) {
        this.evaluator = evaluator;
        this.deploymentTypeRepository = deploymentTypeRepository;
    }

    public Results evaluate(Map<String, Integer> dataFromForm) {

        Results results = new Results();

        List<DeploymentType> deploymentTypes = deploymentTypeRepository.findAll().stream()
                .filter(deploymentType -> deploymentType.getVisible() && !deploymentType.getReferenceDeployments().isEmpty())
                .sorted(Comparator.comparing(DeploymentType::getOrder))
                .collect(Collectors.toList());
        List<ReferenceDeployment> readyReckonerReferenceDeployments = new ArrayList<>();

        deploymentTypes.forEach(deploymentType -> {
            if(!deploymentType.getReferenceDeployments().isEmpty()) {
                ReferenceDeployment readyReckonerForm = new ReferenceDeployment();
                readyReckonerForm.setName("Ready_Reckoner_" + deploymentType.getSizeKey());
                readyReckonerForm.setDescription("Deployment created with the data from the Ready Reckoner Form.");
                readyReckonerForm.setDeploymentType(deploymentType);
                readyReckonerReferenceDeployments.add(readyReckonerForm);
            }
        });


        List<Repository> repositories = evaluator.evaluateAllResourceModelsForReadyReckoner(readyReckonerReferenceDeployments, dataFromForm);

        List<Repository> mediationRepositories = repositories.stream()
                .filter(repo -> repo.getMediationComponent() && repo.getResourceModel() != null)
                .collect(Collectors.toList());

        List<KeyDimensioningValue> keyDimensioningValues = repositories.stream()
                .map(Repository::getKeyDimensioningValues)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        deploymentTypes.forEach(deploymentType -> {

            DeploymentResults deploymentResults = new DeploymentResults();

            deploymentResults.setName(deploymentType.getDisplayName());
            deploymentResults.setDeploymentSize(deploymentType.getSizeKey());

            AtomicBoolean supported = new AtomicBoolean(true);

            mediationRepositories.forEach(repository -> {
                DisplayValue value = new DisplayValue();
                try {
                    value.setName(repository.getName());
                    value.setLimit(repository.getInstanceLimits().get(deploymentType.getSizeKey()).doubleValue());
                    value.setValue(repository
                            .getResourceModel().getScaleUnit()
                            .getRequired_instances()
                            .get("Ready_Reckoner_" + deploymentType.getSizeKey()));

                    deploymentResults.getMediationComponents().add(value);

                    //if value is null then count it as 0
                    if(value.getValue() == null) {
                        value.setValue(0.0);
                    }

                    if (value.getValue() > value.getLimit()) {
                        supported.set(false);
                    }

                } catch (Exception e) {
                    results.getErrors().put(
                            deploymentType.getSizeKey() + " :: " + value.getName(),
                            value.toString() + " --> " + e
                    );
                    supported.set(false);
                }

            });

            keyDimensioningValues.forEach(keyDimensioningValue -> {
                DisplayValue value = new DisplayValue();
                try {
                    value.setName(keyDimensioningValue.getName());
                    value.setLimit(keyDimensioningValue.getCapacitiesPerDeploymentTypes().get(deploymentType.getSizeKey()));
                    value.setValue(keyDimensioningValue.getTotals().get("Ready_Reckoner_" + deploymentType.getSizeKey()));

                    deploymentResults.getKeyDimensioningValues().add(value);

                    //if value is null then count it as 0
                    if(value.getValue() == null) {
                        value.setValue(0.0);
                    }

                    if (value.getValue() > value.getLimit()) {
                        supported.set(false);
                    }

                } catch (Exception e) {
                    results.getErrors().put(
                            deploymentType.getSizeKey() + " :: " + value.getName(),
                            value.toString() + " --> " + e
                    );
                    supported.set(false);
                }

            });

            if (supported.get()) {
                results.getSupported().add(deploymentResults);
            } else {
                results.getNotSupported().add(deploymentResults);
            }

            Optional<Repository> enmNetworkSize = repositories.stream().filter(repo -> repo.getProject().equals(enmNetworkSizeProject)).findFirst();
            enmNetworkSize.ifPresent(repository ->
                    repository.getResourceModel().getDeploymentDependencies().forEach(deploymentDependency -> {
                        for (LoadConversionFormula loadConversionFormula : deploymentDependency.getLoadConversionFormulae()) {
                            if (loadConversionFormula.getName().equalsIgnoreCase("numberOfNodes")) {
                                results.setTotalNumberOfElements(loadConversionFormula.getValues().get("Ready_Reckoner_" + deploymentType.getSizeKey()));
                                return;
                            }
                        }
                    }));

        });

        return results;
    }
}
