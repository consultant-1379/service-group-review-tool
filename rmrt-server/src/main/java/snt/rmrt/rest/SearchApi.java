package snt.rmrt.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.GenericLoadDriver;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadDriver;
import snt.rmrt.models.rmrt.repository.resourceModel.Property;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.ScaleUnit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Unit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Units;
import snt.rmrt.repositories.DeploymentDependantLoadDriverRepository;
import snt.rmrt.repositories.GenericLoadDriverRepository;
import snt.rmrt.repositories.RepositoryRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/search")
public class SearchApi {

    private final GenericLoadDriverRepository genericLoadDriverRepository;
    private final RepositoryRepository repositoryRepository;
    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    @Autowired
    public SearchApi(RepositoryRepository repositoryRepository,
                     GenericLoadDriverRepository genericLoadDriverRepository, DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository) {
        this.repositoryRepository = repositoryRepository;
        this.genericLoadDriverRepository = genericLoadDriverRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
    }

    @GetMapping("name/{nameTerm}")
    public Map<String, List<?>> searchByName(@PathVariable String nameTerm) {
        Map<String, List<?>> results = new HashMap<>();
        final String name = nameTerm.toLowerCase();

        List<Repository> repositories = repositoryRepository.findAll().stream()
                .filter(repository -> repository.getResourceModel() != null)
                .sorted(Comparator.comparing(Repository::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        List<Repository> foundRepositories = new ArrayList<>();
        RepositoryLoop:
        for (Repository repository : repositories) {
            for (Property property : repository.getResourceModel().getProperties()) {
                if (property.getName().toLowerCase().contains(name)) {
                    foundRepositories.add(repository);
                    continue RepositoryLoop;
                }
            }

            for (LoadDriver loadDriver : repository.getResourceModel().getLoadDrivers()) {
                if (loadDriver.getName().toLowerCase().contains(name)) {
                    foundRepositories.add(repository);
                    continue RepositoryLoop;
                }
            }

            for (DeploymentDependency deploymentDependency : repository.getResourceModel().getDeploymentDependencies()) {
                for (LoadConversionFormula loadConversionFormula : deploymentDependency.getLoadConversionFormulae()) {
                    if (loadConversionFormula.getName().toLowerCase().contains(name)) {
                        foundRepositories.add(repository);
                        continue RepositoryLoop;
                    }
                }
            }
        }
        results.put("repositories", foundRepositories);

        List<GenericLoadDriver> genericLoadDrivers = genericLoadDriverRepository.findAll().stream()
                .filter(genericLoadDriver -> genericLoadDriver.getName().toLowerCase().contains(name))
                .sorted(Comparator.comparing(GenericLoadDriver::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        results.put("genericLoadDrivers", genericLoadDrivers);

        List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers = deploymentDependantLoadDriverRepository.findAll().stream()
                .filter(deploymentDependantLoadDriver -> deploymentDependantLoadDriver.getName().toLowerCase().contains(name))
                .sorted(Comparator.comparing(DeploymentDependantLoadDriver::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        results.put("deploymentDependantLoadDrivers", deploymentDependantLoadDrivers);


        return results;
    }

    @GetMapping("formula/{formulaTerm}")
    public Map<String, Set<?>> searchByFormula(@PathVariable String formulaTerm) {
        Map<String, Set<?>> results = new HashMap<>();
        final String formula = formulaTerm.toLowerCase();
        List<Repository> repositories = repositoryRepository.findAll().stream()
                .filter(repository -> repository.getResourceModel() != null)
                .collect(Collectors.toList());

        Set<Repository> foundRepositories = new HashSet<>();
        RepositoryLoop:
        for (Repository repository : repositories) {

            ScaleUnit scaleUnit = repository.getResourceModel().getScaleUnit();
            List<Units> bothUnits = Arrays.asList(scaleUnit.getMinimumUnit(), scaleUnit.getOptimalUnit());
            List<Unit> units = new ArrayList<>();
            bothUnits.forEach(unit -> {
                units.add(unit.getProfile());
                units.add(unit.getConversion());
            });
            List<String> collect = units.stream()
                    .map(unit -> Arrays.asList(
                            unit.getCpuCores(),
                            unit.getMemory(),
                            unit.getCpuMinutes(),
                            unit.getPeakCpuMinutes()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            for (String s : collect) {
                if(s.toLowerCase().contains(formula)) {
                    foundRepositories.add(repository);
                    continue RepositoryLoop;
                }
            }

            for (DeploymentDependency deploymentDependency : repository.getResourceModel().getDeploymentDependencies()) {
                for (LoadConversionFormula loadConversionFormula : deploymentDependency.getLoadConversionFormulae()) {
                    if (loadConversionFormula.getFormula().toLowerCase().contains(formula)) {
                        foundRepositories.add(repository);
                        continue RepositoryLoop;
                    }
                }
            }
        }
        results.put("repositories", foundRepositories);

        return results;
    }

}