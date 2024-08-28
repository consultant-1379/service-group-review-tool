package snt.rmrt.services.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.referenceDeployment.GenericLoadDriver;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;
import snt.rmrt.models.rmrt.repository.resourceModel.Property;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Unit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Units;
import snt.rmrt.repositories.GenericLoadDriverRepository;
import snt.rmrt.repositories.RepositoryRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormulaParser {

    private final RepositoryRepository repositoryRepository;
    private final GenericLoadDriverRepository genericLoadDriverRepository;

    public FormulaParser(RepositoryRepository repositoryRepository,
                         GenericLoadDriverRepository genericLoadDriverRepository) {
        this.repositoryRepository = repositoryRepository;
        this.genericLoadDriverRepository = genericLoadDriverRepository;
    }

    public void parseFormula(Repository repository) {

        List<Repository> repositories = repositoryRepository.findAll();
        repositories.stream()
                .filter(repo->repo.getProject().equals(repository.getProject()))
                .findFirst()
                .ifPresent(repositories::remove);
        repositories.forEach(repo-> repo.getChangeRequests().clear());

        repositories.add(repository);

        parseAllFormula(repositories);
    }

    public void parseAllFormula(List<Repository> repositories) {

        Pattern pattern = Pattern.compile("\\b[\\w]+\\b");
        List<GenericLoadDriver> genericLoadDrivers = genericLoadDriverRepository.findAll();

        // Masters
        List<ResourceModel> resourceModels = repositories.stream()
                .map(Repository::getResourceModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<LoadConversionFormula> loadConversionFormulae
                = resourceModels.stream()
                .map(ResourceModel::getDeploymentDependencies)
                .flatMap(Collection::stream)
                .map(DeploymentDependency::getLoadConversionFormulae)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Property> parameters
                = resourceModels.stream()
                .map(ResourceModel::getProperties)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        parseScaleUnits(pattern, resourceModels, loadConversionFormulae, parameters, genericLoadDrivers);
        parseLoadConversionFormula(pattern, loadConversionFormulae, parameters, genericLoadDrivers);


        // Change Requests
        repositories.forEach(repo -> {
            repo.getChangeRequests().forEach(changeRequest -> {

                List<ResourceModel> modifiedListOfResourceModels = new ArrayList<>(resourceModels);
                modifiedListOfResourceModels.remove(repo.getResourceModel());
                modifiedListOfResourceModels.add(changeRequest.getResourceModel());

                List<LoadConversionFormula> modifiedListOfLoadConversionFormulae
                        = modifiedListOfResourceModels.stream()
                        .map(ResourceModel::getDeploymentDependencies)
                        .flatMap(Collection::stream)
                        .map(DeploymentDependency::getLoadConversionFormulae)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                List<Property> modifiedListOfParameters
                        = modifiedListOfResourceModels.stream()
                        .map(ResourceModel::getProperties)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

                parseScaleUnits(pattern, modifiedListOfResourceModels, modifiedListOfLoadConversionFormulae, modifiedListOfParameters, genericLoadDrivers);
                parseLoadConversionFormula(pattern, modifiedListOfLoadConversionFormulae, modifiedListOfParameters, genericLoadDrivers);

            });
        });
    }


    private void parseScaleUnits(Pattern pattern, List<ResourceModel> resourceModels, List<LoadConversionFormula> loadConversionFormulae, List<Property> parameters, List<GenericLoadDriver> genericLoadDrivers) {
        resourceModels.forEach(resourceModel -> {
            List<Units> minAndOptUnits = Arrays.asList(
                    resourceModel.getScaleUnit().getMinimumUnit(),
                    resourceModel.getScaleUnit().getOptimalUnit()
            );
            List<Unit> allUnits = minAndOptUnits.stream()
                    .map(units -> Arrays.asList(units.getProfile(), units.getConversion()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            allUnits.forEach(unit -> {
                AtomicReference<Matcher> matcherCpuCores        = new AtomicReference<>(pattern.matcher(unit.getParsedCpuCores()));
                AtomicReference<Matcher> matcherMemory          = new AtomicReference<>(pattern.matcher(unit.getParsedMemory()));
                AtomicReference<Matcher> matcherCpuMinutes      = new AtomicReference<>(pattern.matcher(unit.getParsedCpuMinutes()));
                AtomicReference<Matcher> matcherPeakCpuMinutes  = new AtomicReference<>(pattern.matcher(unit.getParsedPeakCpuMinutes()));

                Map<UnitType, AtomicReference<Matcher>> unitsAndMatcher = new HashMap<>();
                unitsAndMatcher.put(UnitType.CPU_CORES, matcherCpuCores);
                unitsAndMatcher.put(UnitType.MEMORY, matcherMemory);
                unitsAndMatcher.put(UnitType.CPU_MINS, matcherCpuMinutes);
                unitsAndMatcher.put(UnitType.PEAK_CPU_MINS, matcherPeakCpuMinutes);

                unitsAndMatcher.forEach((unitType, matcher) -> {
                    while (matcher.get().find()) {

                        final String matchedString = matcher.get().group();

                        // inside resource model
                        resourceModel.getDeploymentDependencies().stream()
                                .map(DeploymentDependency::getLoadConversionFormulae)
                                .flatMap(Collection::stream)
                                .filter(ldc -> ldc.getName().equalsIgnoreCase(matchedString))
                                .forEach(ldc -> {
                                    String value = ldc.getParsedFormula();
                                    updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, value, resourceModel);
                                });

                        resourceModel.getProperties().stream()
                                .filter(param -> param.getName().equalsIgnoreCase(matchedString))
                                .forEach(param -> {
                                    String value = param.getDefaultValue().toString();
                                    updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, value, resourceModel);
                                });




                        // outside resource model
                        List<LoadConversionFormula> matchedLdcs = loadConversionFormulae.stream()
                                .filter(ldc -> ldc.getName().equalsIgnoreCase(matchedString))
                                .collect(Collectors.toList());

                        // Either Accumulated Load Drivers or standalone
                        if (matchedLdcs.size() > 1) {
                            List<String> accumulated = new ArrayList<>();
                            for (LoadConversionFormula l : matchedLdcs) {
                                accumulated.add(l.getParsedFormula());
                            }
                            updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, "(" + StringUtil.join(accumulated, "+") + ")", resourceModel);
                        } else {
                            matchedLdcs.forEach(ldc ->
                                    updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, ldc.getParsedFormula(), resourceModel));
                        }


                        List<Property> matchedParameters = parameters.stream()
                                .filter(prop -> prop.getName().equalsIgnoreCase(matchedString))
                                .collect(Collectors.toList());

                        // Possible warning -> if more than 1 add error and specify which is used.
                        matchedParameters.stream().findFirst().ifPresent(firstProp ->
                            updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, firstProp.getDefaultValue().toString(), resourceModel)
                        );

                        genericLoadDrivers.stream()
                                .filter(generic -> generic.getName().equalsIgnoreCase(matchedString))
                                .forEach(generic ->
                                        updateScaleUnitFormula(unit, unitType, matcher, pattern, matchedString, generic.getValue().toString(), resourceModel)
                                );
                    }
                });
            });

        });
    }
    private void updateScaleUnitFormula(Unit unit, UnitType unitType, AtomicReference<Matcher> matcher, Pattern pattern, String matchedString, String value, ResourceModel resourceModel) {

        try {
            switch (unitType) {
                case CPU_CORES:
                    unit.setParsedCpuCores(unit.getParsedCpuCores().replaceAll("\\b"+matchedString+"\\b", value));
                    matcher.set(pattern.matcher(unit.getParsedCpuCores()));
                    break;
                case CPU_MINS:
                    unit.setParsedCpuMinutes(unit.getParsedCpuMinutes().replaceAll("\\b"+matchedString+"\\b", value));
                    matcher.set(pattern.matcher(unit.getParsedCpuMinutes()));
                    break;
                case MEMORY:
                    unit.setParsedMemory(unit.getParsedMemory().replaceAll("\\b"+matchedString+"\\b", value));
                    matcher.set(pattern.matcher(unit.getParsedMemory()));
                    break;
                case PEAK_CPU_MINS:
                    unit.setParsedPeakCpuMinutes(unit.getParsedPeakCpuMinutes().replaceAll("\\b"+matchedString+"\\b", value));
                    matcher.set(pattern.matcher(unit.getParsedPeakCpuMinutes()));
                    break;
            }
        } catch(Exception e) {
            log.error("Error parsing scale unit formula: "+ unitType.name()+"::"+unit.toString() + " for resource model " + resourceModel.getName());
            log.error(e.getMessage());
        }

    }

    private void parseLoadConversionFormula(Pattern pattern, List<LoadConversionFormula> loadConversionFormulae, List<Property> parameters, List<GenericLoadDriver> genericLoadDrivers) {
        loadConversionFormulae.forEach(loadConversionFormula -> {
            AtomicReference<Matcher> matcher = new AtomicReference<>(pattern.matcher(loadConversionFormula.getParsedFormula()));
            while (matcher.get().find()) {

                final String matchedString = matcher.get().group();

                List<LoadConversionFormula> matchedLdcs = loadConversionFormulae.stream()
                        .filter(ldc -> ldc.getName().equalsIgnoreCase(matchedString))
                        .collect(Collectors.toList());

                // Either Accumulated Load Drivers or standalone
                if (matchedLdcs.size() > 1) {
                    List<String> accumulated = new ArrayList<>();
                    for (LoadConversionFormula l : matchedLdcs) {
                        accumulated.add(l.getParsedFormula());
                    }
                    updateLdcFormula(matcher, pattern, loadConversionFormula, matchedString, "(" + StringUtil.join(accumulated, "+") + ")");

                } else {
                    matchedLdcs.forEach(ldc -> updateLdcFormula(matcher, pattern, loadConversionFormula, matchedString, ldc.getParsedFormula())
                    );
                }


                List<Property> matchedParameters = parameters.stream()
                        .filter(prop -> prop.getName().equalsIgnoreCase(matchedString))
                        .collect(Collectors.toList());

                // Possible warning -> if more than 1 add error and specify which is used.
                matchedParameters.stream().findFirst().ifPresent(firstProp -> {
                    if(matchedParameters.size()>1) {
                        loadConversionFormula.getDeploymentDependency().getResourceModel()
                                .getErrorMessages().add(
                                "Duplicate Parameters detected:" + firstProp.getName()
                                        + " -> using from " + firstProp.getResourceModel().getRepository().getName()
                        );
                    }
                    updateLdcFormula(matcher, pattern, loadConversionFormula, matchedString, firstProp.getDefaultValue().toString());
                });

                genericLoadDrivers.stream()
                        .filter(generic -> generic.getName().equalsIgnoreCase(matchedString))
                        .forEach(generic ->
                                updateLdcFormula(matcher, pattern, loadConversionFormula, matchedString, generic.getValue().toString())
                        );
            }
        });
    }
    private void updateLdcFormula(AtomicReference<Matcher> matcher, Pattern pattern, LoadConversionFormula loadConversionFormula, String matchedString, String value) {
        try {
            loadConversionFormula.setParsedFormula(
                    loadConversionFormula.getParsedFormula().replaceAll("\\b"+matchedString+"\\b", value)
            );
            matcher.set(pattern.matcher(loadConversionFormula.getParsedFormula()));
        } catch (Exception e) {
            log.error("Error parsing ldc formula: "+loadConversionFormula.getName());
            log.error(e.getMessage());
        }

    }

}

enum UnitType {
    CPU_CORES, MEMORY, CPU_MINS, PEAK_CPU_MINS
}
