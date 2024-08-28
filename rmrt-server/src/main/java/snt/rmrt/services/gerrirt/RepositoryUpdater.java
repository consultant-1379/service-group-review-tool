package snt.rmrt.services.gerrirt;

import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.client.ListChangesOption;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMapping;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;
import snt.rmrt.models.rmrt.repository.ChangeRequest;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadDriver;
import snt.rmrt.repositories.*;
import snt.rmrt.services.evaluator.FormulaParser;
import snt.rmrt.services.evaluator.RmrtEvaluator;
import snt.rmrt.tools.RMXmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RepositoryUpdater extends GerritRmrtApi {

    private final RepositoryRepository repositoryRepository;
    private final GenericLoadDriverRepository genericLoadDriverRepository;
    private final DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository;

    private final FileSystemMappingsRepository fileSystemMappingsRepository;
    private final KeyDimensioningValuesRepository keyDimensioningValuesRepository;

    private final FormulaParser formulaParser;

    private final RmrtEvaluator evaluator;

    @Autowired
    public RepositoryUpdater(RepositoryRepository repositoryRepository,
                             GenericLoadDriverRepository genericLoadDriverRepository,
                             DeploymentDependantLoadDriverRepository deploymentDependantLoadDriverRepository,
                             FileSystemMappingsRepository fileSystemMappingsRepository,
                             KeyDimensioningValuesRepository keyDimensioningValuesRepository,
                             FormulaParser formulaParser, RmrtEvaluator evaluator) {
        this.repositoryRepository = repositoryRepository;
        this.genericLoadDriverRepository = genericLoadDriverRepository;
        this.deploymentDependantLoadDriverRepository = deploymentDependantLoadDriverRepository;
        this.fileSystemMappingsRepository = fileSystemMappingsRepository;
        this.keyDimensioningValuesRepository = keyDimensioningValuesRepository;
        this.formulaParser = formulaParser;
        this.evaluator = evaluator;
    }

    // Every 3 hours
    @Scheduled(fixedRate = 60000 * 60 * 3, initialDelay = 3000)
    private void updateAll() {
        log.info("Updating all Resource Models");
        List<Repository> repositories = repositoryRepository.findAll();

        log.info("Downloading files");
        repositories.parallelStream().forEach(this::downloadFiles);

        log.info("Parsing files");
        repositories.parallelStream().forEach(this::parseXml);
        log.info("Parsing formulae");
        formulaParser.parseAllFormula(repositories);

        log.info("Evaluating Resource Models");
        evaluator.evaluateAllResourceModels(repositories);

        log.info("Searching for Key Dims");
        repositories.forEach(this::checkForKeyDims);
        log.info("Searching for File Systems");
        repositories.forEach(this::checkForFileSystems);

        log.info("Finding new Load Drivers...");
        Set<String> names = getAllNames(repositories);
        repositories.stream()
                .map(Repository::getChangeRequests)
                .flatMap(Collection::stream)
                .forEach(changeRequest ->
                        checkForNewLoadDrivers(changeRequest, names)
                );

        log.info("Saving...");
        repositories.forEach(repository -> {
            try {
                repository.setLastUpdated(LocalDateTime.now());
                repositoryRepository.save(repository);
            } catch (Exception e) {
                log.error(repository.getName() + " -> " + e);
            }
        });

        log.info("Finished Updating Repositories");
    }

    public void updateRepository(Repository repository) {
        log.info("Updating "+repository.getName());

        log.info("Downloading file");
        downloadFiles(repository);

        log.info("Parsing file");
        parseXml(repository);
        log.info("Parsing formulae...");
        formulaParser.parseFormula(repository);

        log.info("Evaluating...");
        evaluator.evaluateRepository(repository);

        log.info("Searching for Key Dims");
        checkForKeyDims(repository);
        log.info("Searching for File Systems");
        checkForFileSystems(repository);

        log.info("Finding new Load Drivers...");
        Set<String> names = getAllNames(repositoryRepository.findAll());
        repository.getChangeRequests()
                .forEach(changeRequest ->
                        checkForNewLoadDrivers(changeRequest, names)
                );

        log.info("Saving...");
        try {
            repository.setLastUpdated(LocalDateTime.now());
            repositoryRepository.save(repository);
        } catch (Exception e) {
            log.error(repository.getName() + " -> " + e);
        }
        log.info("Finished Updating "+repository.getName());
    }

    private void downloadFiles(Repository repository) {
        downloadMaster(repository);
        downloadChangeRequests(repository);
    }
    private void downloadMaster(Repository repository) {
        Path path = Paths.get("RMRT","ResourceModels", repository.getProject(), repository.getBranch() + ".xml");

        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
            }
            final String file = getGerritApi()
                    .projects().name(repository.getProject())
                    .branch(repository.getBranch())
                    .file(repository.getFilePath())
                    .asString();
            Files.write(path, Base64.getDecoder().decode(file));
        }
        catch (IOException | RestApiException e) {
            log.error("Error retrieving file for: " + repository.getName() + " -> " + e.getMessage());
        }
    }
    private void downloadChangeRequests(Repository repository) {
        try {
            List<ChangeRequest> changeRequests = new ArrayList<>();
            List<ChangeInfo> changeInfos = getChanges(repository);

            changeInfos.parallelStream().forEach(changeInfo -> {
                try {

                    ChangeApi changeApi = getGerritApi().changes().id(changeInfo.changeId);
                    RevisionApi revisionApi = changeApi.revision(changeInfo.currentRevision);
                    final Map<String, FileInfo> fileApi = revisionApi.files();
                    for (final Map.Entry<String, FileInfo> entry : fileApi.entrySet()) {
                        final String key = entry.getKey();
                        if (key.contains(repository.getFilePath())) {
                            Path path = Paths.get("RMRT", "ResourceModels", changeInfo.project, changeInfo.changeId, "changed.xml");
                            if (!Files.exists(path)) {
                                Files.createDirectories(path.getParent());
                            }
                            Files.write(path, Base64.getDecoder().decode(revisionApi.file(key).content().asString()));
                            ChangeRequest changeRequest = new ChangeRequest(repository);
                            changeRequest.setChangeId(changeInfo.changeId);
                            if(changeInfo.revisions.isEmpty()) {
                                throw new Exception("No revision set with Commit in Gerrit. Please resubmit the change request.");
                            }
                            changeRequest.setRevisionId(changeInfo.currentRevision);
                            changeRequest.setStatus(changeInfo.status.name());
                            changeRequest.setOwner(changeInfo.owner.name + " <X>".replace("X", changeInfo.owner.email));

                            changeRequests.add(changeRequest);
                        }
                    }

                } catch (RestApiException restApiException) {
                    log.error("Could not retrieve changes for: " + repository.getName() + " -> " + restApiException.getMessage());
                } catch (IOException ioException) {
                    log.error("Error saving XML file from change: " + repository.getName() + ":" + changeInfo.changeId + " -> " + ioException.getMessage());
                } catch (Exception e) {
                    log.error("Error in change request: " + repository.getName() + ":" + changeInfo.changeId + " -> " + e.getMessage());

                }
            });

            repository.getChangeRequests().clear();
            repository.getChangeRequests().addAll(changeRequests);

        } catch (RestApiException restApiException) {
            log.error("Could not retrieve changes for: " + repository.getName() + " -> " + restApiException.getMessage());
        }
    }
    private List<ChangeInfo> getChanges(Repository repository) throws RestApiException {
        final EnumSet<ListChangesOption> options = EnumSet.of(
                ListChangesOption.CURRENT_REVISION,
                ListChangesOption.DETAILED_ACCOUNTS);

        return getGerritApi().changes().query(
                "status:open" +
                        "+file:" + repository.getFilePath() +
                        "+project:" + repository.getProject() +
                        "+branch:" + repository.getBranch())
                .withLimit(100)
                .withOptions(options)
                .get();
    }

    private void parseXml(Repository repository) {
        parseMaster(repository);
        parseChangeRequests(repository);
    }
    private void parseMaster(Repository repository) {
        try {
            Path path = Paths.get("RMRT", "ResourceModels",
                    repository.getProject(), repository.getBranch() + ".xml");

            final String name = repository.getProject();
            final String owningElement = repository.getProject();
            final boolean isMaster = true;
            RMXmlParser rmXmlParser = new RMXmlParser(path.toFile(), name, owningElement, isMaster);
            repository.setResourceModel(rmXmlParser.getResourceModel());
        } catch (Exception e) {
                log.error("Parsing Error: " + repository.getName() + " -> " + e.getMessage());
            }
    }
    private void parseChangeRequests(Repository repository) {
        repository.getChangeRequests().forEach(changeRequest -> {
            try {
                Path path = Paths.get("RMRT", "ResourceModels",
                        repository.getProject(), changeRequest.getChangeId(), "changed.xml");

                final String name = repository.getProject();
                final String owningElement = changeRequest.getChangeId();
                final boolean isMaster = false;
                RMXmlParser rmXmlParser = new RMXmlParser(path.toFile(), name, owningElement, isMaster);

                changeRequest.setResourceModel(rmXmlParser.getResourceModel());
            } catch (Exception e) {
                log.error("Parsing Error: " + repository.getName() + "::" + changeRequest.getChangeId() + " -> " + e.getMessage());
            }
        });
    }

    private void checkForKeyDims(Repository repository) {
        List<KeyDimensioningValue> keyDims = keyDimensioningValuesRepository.findAll();
        repository.getKeyDimensioningValues().clear();

        keyDims.forEach(keyDimensioningValue -> {
            repository.getResourceModel()
                    .getDeploymentDependencies().stream()
                    .map(DeploymentDependency::getLoadConversionFormulae)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
                    .forEach(ldc -> {
                        if (keyDimensioningValue.getName().equalsIgnoreCase(ldc.getName())) {
                            repository.getKeyDimensioningValues().add(keyDimensioningValue);
                            keyDimensioningValue.getRepositories().add(repository);
                        }
                    });
        });
    }
    private void checkForFileSystems(Repository repository) {
        List<FileSystemMapping> fileSystemMappings = fileSystemMappingsRepository.findAll();
        repository.getFileSystemMappings().clear();

        fileSystemMappings.forEach(fileSystemMapping -> {
            repository.getResourceModel()
                    .getDeploymentDependencies().stream()
                    .map(DeploymentDependency::getLoadConversionFormulae)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
                    .forEach(ldc -> {
                        if (fileSystemMapping.allMappings().contains(ldc.getFormula())) {
                            repository.getFileSystemMappings().add(fileSystemMapping);
                            fileSystemMapping.getRepositories().add(repository);
                        }
                    });
        });
    }

    private void checkForNewLoadDrivers(ChangeRequest changeRequest, Set<String> names) {

        Set<String> presentNames = changeRequest.getResourceModel().names();
        presentNames.removeAll(names);

        if(!presentNames.isEmpty()) {
            for (String presentName : presentNames) {
                Optional<LoadConversionFormula> optLdc = changeRequest.getResourceModel()
                        .getDeploymentDependencies().stream()
                        .map(DeploymentDependency::getLoadConversionFormulae)
                        .flatMap(Collection::stream)
                        .filter(ldc -> ldc.getName().equalsIgnoreCase(presentName))
                        .findFirst();
                if(!optLdc.isPresent()) {
                    Optional<LoadDriver> loadDriver = changeRequest.getResourceModel().getLoadDrivers()
                            .stream().filter(ld -> ld.getName().equalsIgnoreCase(presentName))
                            .findFirst();
                    loadDriver.ifPresent(ld -> changeRequest.getNewLoadDrivers().add(ld));
                }
            }
        }

    }
    private Set<String> getAllNames(List<Repository> repositories) {
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.addAll(deploymentDependantLoadDriverRepository.findAllIds());
        names.addAll(genericLoadDriverRepository.findAllIds());
        repositories.stream()
                .map(Repository::getResourceModel)
                .filter(Objects::nonNull)
                .forEach(resourceModel ->
                        names.addAll(resourceModel.names()));
        return names;
    }


}
