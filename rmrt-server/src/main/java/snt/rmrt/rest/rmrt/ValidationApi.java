package snt.rmrt.rest.rmrt;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.repository.ChangeRequest;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;
import snt.rmrt.repositories.RepositoryRepository;
import snt.rmrt.services.evaluator.FormulaParser;
import snt.rmrt.services.evaluator.RmrtEvaluator;
import snt.rmrt.tools.RMXmlParser;

import javax.websocket.server.PathParam;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("api/validation")
public class ValidationApi {

    @Value("${temp.dir}")
    private String tempDir;

    private final RmrtEvaluator rmrtEvaluator;
    private final FormulaParser formulaParser;
    private final RepositoryRepository repositoryRepository;

    @Autowired
    public ValidationApi(RmrtEvaluator evaluator, FormulaParser formulaParser, RepositoryRepository repositoryRepository) {
        this.rmrtEvaluator = evaluator;
        this.formulaParser = formulaParser;
        this.repositoryRepository = repositoryRepository;
    }

    @PostMapping
    public ChangeRequest validate(@PathParam("project") String project, @RequestBody String xml) {

        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setChangeId("Uploaded File: " + LocalDateTime.now().toString());

        repositoryRepository.findById(project)
                .ifPresent(repository -> {
                    try {
                        Path path = Paths.get(tempDir);
                        if (Files.notExists(path)) {
                            Files.createDirectories(path);
                        }

                        Path tempPath = Files.createTempFile(path, "upload-", ".xml");
                        Files.write(tempPath, xml.getBytes());

                        ResourceModel resourceModel = new RMXmlParser(tempPath.toFile(), changeRequest.getChangeId(), Strings.EMPTY, false).getResourceModel();
                        changeRequest.setResourceModel(resourceModel);

                        repository.getChangeRequests().add(changeRequest);
                        formulaParser.parseFormula(repository);

                        rmrtEvaluator.evaluateRepository(repository);

                    } catch (Exception e) {
                        log.warn("Error parsing for manual validation: " + e.getMessage());
                    }
                });
        //TODO
        // Not evaluating properly
        return changeRequest;
    }

}
