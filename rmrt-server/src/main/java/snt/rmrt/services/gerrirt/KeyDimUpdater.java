package snt.rmrt.services.gerrirt;

import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.repositories.KeyDimensioningValuesRepository;
import snt.rmrt.repositories.ReferenceDeploymentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeyDimUpdater extends GerritRmrtApi {

    @Value("${keyDimensioning.project}")
    private String keyDimProject;
    @Value("${keyDimensioning.filePath}")
    private String keyDimFilePath;

    private final KeyDimensioningValuesRepository keyDimensioningValuesRepository;
    private final ReferenceDeploymentRepository referenceDeploymentRepository;

    @Autowired
    public KeyDimUpdater(KeyDimensioningValuesRepository keyDimensioningValuesRepository, ReferenceDeploymentRepository referenceDeploymentRepository) {
        this.keyDimensioningValuesRepository = keyDimensioningValuesRepository;
        this.referenceDeploymentRepository = referenceDeploymentRepository;
    }

    // Every 3 hours
    @Scheduled(fixedRate = 60000 * 60 * 3)
    private void update() {
        log.info("Updating Key Dimensioning Values & File Systems");
        try {
            getKeyDimensioningValues();
        } catch (Exception exception) {
            log.error("Key Dimensioning Values: " + exception.getMessage());
        }
        log.info("Finished updating Key Dimensioning Values");
    }

    private void getKeyDimensioningValues() throws RestApiException, IOException {
        Path path = Paths.get("RMRT", "KeyDimensioningValues", "file.json");

        final String file = new String(Base64.getDecoder().decode(
                getGerritApi().projects().name(keyDimProject).branch(branch).file(keyDimFilePath).asString()));
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, file.getBytes());

        final JsonParser jsonParser = new JsonParser();
        final JsonElement json = jsonParser.parse(file);
        final JsonElement keyDimValues = json.getAsJsonObject().get("key_dimensioning_values");

        List<KeyDimensioningValue> newKeyDimensioningValues = new ArrayList<>();
        List<KeyDimensioningValue> oldKeyDimensioningValues = keyDimensioningValuesRepository.findAll();

        List<ReferenceDeployment> allReferenceDeployments = referenceDeploymentRepository.findAll();


        keyDimValues.getAsJsonArray().forEach(key -> {
            try {
                final String loadDriverName = (key.getAsJsonObject().get("name").getAsString()).trim();

                KeyDimensioningValue keyDimensioningValue = new KeyDimensioningValue();
                keyDimensioningValue.setName(loadDriverName);

                key.getAsJsonObject().get("deployment").getAsJsonArray().forEach(sizeCap -> {

                    final String deploymentSize = sizeCap.getAsJsonObject().get("enm_deployment_type").getAsString();
                    final double capacity = sizeCap.getAsJsonObject().get("capacity").getAsDouble();

                    keyDimensioningValue.getCapacitiesPerDeploymentTypes().put(deploymentSize, capacity);

                    List<ReferenceDeployment> refs = allReferenceDeployments.stream()
                            .filter(ref -> ref.getDeploymentType().getSizeKey().equalsIgnoreCase(deploymentSize)).collect(Collectors.toList());
                    refs.forEach(ref -> {
                        keyDimensioningValue.getCapacities().put(ref.getName(), capacity);
                    });

                });
                newKeyDimensioningValues.add(keyDimensioningValue);

            } catch (Exception e) {
                log.error("Error parsing Key Dimensioning value: " + key.toString());
            }

        });

        try {

            keyDimensioningValuesRepository.saveAll(newKeyDimensioningValues);

            oldKeyDimensioningValues.removeAll(newKeyDimensioningValues);
            keyDimensioningValuesRepository.deleteAll(oldKeyDimensioningValues);

        } catch (Exception exception) {
            log.error("Key Dimensioning Values: " + exception.getMessage());
        }
    }


}
