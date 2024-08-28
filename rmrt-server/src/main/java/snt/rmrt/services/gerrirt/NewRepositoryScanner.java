package snt.rmrt.services.gerrirt;

import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.client.ListChangesOption;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;
import snt.rmrt.repositories.RepositoryRepository;
import snt.rmrt.tools.RMXmlParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NewRepositoryScanner extends GerritRmrtApi {

    private final RepositoryRepository repositoryRepository;

    public NewRepositoryScanner(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Value("${gerrit.projectRegex}")
    private String projectsLocationRegex;
    @Value("${gerrit.filePathRegex}")
    private String filePathRegex;
    @Value("${gerrit.newRepositoriesRegex}")
    private String newRepositoriesRegex;

    // Every 2 hours, 1 hour delay
    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    private void searchNewProjectAndFilePath() {
        log.info("Searching for new Repositories");

        List<Repository> repositories = repositoryRepository.findAll();

        final String status = "open";
        String branch = "master";
        final Changes.QueryRequest changeInfosQuery = getGerritApi().changes()
                .query("status:" + status +
                        "+project:" + projectsLocationRegex +
                        "+file:" + filePathRegex +
                        "+branch:" + branch)
                .withOption(ListChangesOption.CURRENT_REVISION);
        try {
            List<ChangeInfo> changeInfos = changeInfosQuery.get();
            changeInfos.parallelStream().forEach(changeInfo -> {
                try {
                    getGerritApi()
                            .changes().id(changeInfo.id)
                            .revision(changeInfo.currentRevision)
                            .files()
                            .keySet()
                            .forEach((filePath) -> {
                                if (filePath.matches(newRepositoriesRegex)) {
                                    try {
                                        final ResourceModel resourceModel = getResourceModelFromChange(changeInfo.project, changeInfo.changeId, changeInfo.currentRevision, filePath);
                                        if (resourceModel.isValidSchema()) {
                                            Repository newRepository = new Repository();
                                            newRepository.setProject(changeInfo.project);
                                            newRepository.setFilePath(filePath);
                                            newRepository.setResourceModel(resourceModel);

                                            if (repositories.stream()
                                                    .noneMatch(repository -> repository.getProject().equals(changeInfo.project))) {
                                                repositoryRepository.save(newRepository);
                                                log.info("Added new Repository: " + newRepository.getProject());
                                            }

                                        }
                                    } catch (Exception ignored) {
                                    }
                                }
                            });
                } catch (RestApiException ignored) {
                }
            });
        } catch (RestApiException exception) {
            log.error(exception.getMessage());
        }
        log.info("Finished searching for new Repositories");
    }
    private ResourceModel getResourceModelFromChange(String project, String changeId, String revisionId, String filePath) throws RestApiException, IOException {

        ChangeApi changeApi = getGerritApi().changes().id(changeId);
        RevisionApi revisionApi = changeApi.revision(revisionId);
        final Map<String, FileInfo> fileApi = revisionApi.files();
        ResourceModel resourceModel = null;
        for (final Map.Entry<String, FileInfo> entry : fileApi.entrySet()) {
            final String key = entry.getKey();
            if (key.contains(filePath)) {
                Path path = Paths.get(project, changeId, revisionId);
                Files.write(path, Base64.getDecoder().decode(revisionApi.file(filePath).content().asString()));
                resourceModel = new RMXmlParser(path.toFile(), project, changeId, false).getResourceModel();
            }
        }
        return resourceModel;
    }

}
