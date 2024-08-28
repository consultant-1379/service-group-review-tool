package snt.rmrt.services.gerrirt;

import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import snt.rmrt.models.rmrt.fileSystems.FileSystem;
import snt.rmrt.models.rmrt.fileSystems.FsType;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentType;
import snt.rmrt.repositories.DeploymentTypeRepository;
import snt.rmrt.repositories.FileSystemsRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileSystemsUpdater extends GerritRmrtApi {

    @Value("${ENMDeploymentSizeProperties.project}")
    private String fileSys_DeploymentSizePropertiesProject;
    @Value("${ENMDeploymentSizeProperties.filePath}")
    private String fileSys_DeploymentSizePropertiesFilePath;

    @Value("${ENMCloudTemplates.project}")
    private String fileSys_CloudTemplatesProject;
    @Value("${ENMCloudTemplates.filePath}")
    private String fileSys_CloudTemplatesFilePath;

    private final FileSystemsRepository fileSystemsRepository;
    private final DeploymentTypeRepository deploymentTypeRepository;

    public FileSystemsUpdater(FileSystemsRepository fileSystemsRepository,
                              DeploymentTypeRepository deploymentTypeRepository) {
        this.fileSystemsRepository = fileSystemsRepository;
        this.deploymentTypeRepository = deploymentTypeRepository;
    }

    // Every 3 hours
    @Scheduled(fixedRate = 60000 * 60 * 3)
    private void update() {
        log.info("Updating File Systems");
        try {
            getFileSystems_ENMCloudTemplates();
        } catch (Exception exception) {
            log.error("File Systems Cloud Templates: " + exception.getMessage());
        }
        try {
            getFileSystems_ENMDeploymentSizeProperties();
        } catch (Exception exception) {
            log.error("File Systems Deployment Size Properties: " + exception.getMessage());
        }
        log.info("Finished updating File Systems");
    }

    private void getFileSystems_ENMDeploymentSizeProperties() throws RestApiException, IOException {

        final String file = new String(Base64.getDecoder().decode(
                getGerritApi().projects()
                        .name(fileSys_DeploymentSizePropertiesProject)
                        .branch(branch)
                        .file(fileSys_DeploymentSizePropertiesFilePath)
                        .asString()));
        Path path = Paths.get("RMRT", "FileSystems", "ENMDeploymentSizeProperties", "file.properties");
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, file.getBytes());

        List<DeploymentType> deploymentTypes = deploymentTypeRepository.findAll();

        final Pattern pattern = Pattern.compile("[\\w_]*=.*");
        final Matcher matcher = pattern.matcher(file);

        Map<String, String[]> rows = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        while (matcher.find()) {
            final String line = matcher.group();

            final String[] splitOnEquals = line.split("=");
            final String parameterName = splitOnEquals[0];
            final String[] values = splitOnEquals[1].split(",");
            rows.put(parameterName, values);
        }

        String[] keys = rows.get("parameterName");

        List<FileSystem> fileSystems = new ArrayList<>();

        // Every row in file
        rows.forEach((name, values) -> {

            FileSystem fileSystem = new FileSystem();
            fileSystem.setName(name);
            fileSystem.setType(FsType.CLOUD);

            for (int i = 0; i < keys.length; i++) {
                    String key = keys[i];

                    List<DeploymentType> matchingDeploymentTypes = deploymentTypes.stream()
                            .filter(deploymentType -> deploymentType.getMappingsForFileSystems().equalsIgnoreCase(key))
                            .collect(Collectors.toList());

                int finalI = i;
                matchingDeploymentTypes.forEach(deploymentType -> {
                        try {
                            Double capacity = Double.parseDouble(convertValueToMB(values[finalI]));
                            deploymentType.getReferenceDeployments().forEach(ref -> {
                                fileSystem.getCapacities().put(ref.getName(), capacity);
                            });
                        } catch (Exception ignore) {
                        }
                    });

            }
            fileSystems.add(fileSystem);
        });

        // Special Mapping for PM Physical
        FileSystem pmFileSystem = new FileSystem();
        pmFileSystem.setName("pm1_pm2");
        pmFileSystem.setType(FsType.CLOUD);

        for (int i = 0; i < keys.length; i++) {
            try {
                String key = keys[i];
                List<DeploymentType> matchingDeploymentTypes = deploymentTypes.stream()
                        .filter(deploymentType -> deploymentType.getMappingsForFileSystems().equalsIgnoreCase(key))
                        .collect(Collectors.toList());

                String pm_1 = rows.get("pm1")[i];
                String pm_2 = rows.get("pm2")[i];

                Double pm1_cap = Double.parseDouble(convertValueToMB(pm_1));
                Double pm2_cap = Double.parseDouble(convertValueToMB(pm_2));
                double pm_total_cap = pm1_cap + pm2_cap;

                int finalI = i;
                matchingDeploymentTypes.forEach(deploymentType -> {
                    try {
                        deploymentType.getReferenceDeployments().forEach(ref -> {
                            pmFileSystem.getCapacities().put(ref.getName(), pm_total_cap);
                        });
                    } catch (Exception ignore) {
                    }
                });

            } catch (Exception ignore) {
            }
        }
        fileSystems.add(pmFileSystem);

        fileSystemsRepository.saveAll(fileSystems);
    }

    private void getFileSystems_ENMCloudTemplates() throws RestApiException, IOException {


        final String file = new String(Base64.getDecoder().decode(
                getGerritApi().projects().name(fileSys_CloudTemplatesProject).branch(branch).file(fileSys_CloudTemplatesFilePath).asString()));
        Path path = Paths.get("RMRT", "FileSystems", "ENMCloudTemplates", "file.csv");
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, file.getBytes());

        List<DeploymentType> deploymentTypes = deploymentTypeRepository.findAll();

        final Pattern pattern = Pattern.compile("(.*)\n");
        final Matcher matcher = pattern.matcher(file);

        Map<String, String[]> rows = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        while (matcher.find()) {
            final String line = matcher.group();
            final String[] splitOnComma = line.split(",");

            final String name_Key = splitOnComma[0];
            final String[] values = new String[splitOnComma.length - 2];
            System.arraycopy(splitOnComma, 1, values, 0, values.length);
            rows.put(name_Key, values);
        }

        List<FileSystem> fileSystems = new ArrayList<>();
        String[] keys = rows.get("enm_deployment_type");

        // Every row in file
        rows.forEach((name, values) -> {
            FileSystem fileSystem = new FileSystem();
            fileSystem.setName(name);
            fileSystem.setType(FsType.CLOUD);

            try {
                for (int i = 0; i < keys.length; i++) {
                    double cap = Double.parseDouble(convertValueToMB(values[i]));
                    // All sizes listed are in GB (without indication)
                    final double capacity = cap * 1024;

                    int finalI = i;
                    List<DeploymentType> matchingDeploymentTypes = deploymentTypes.stream()
                            .filter(deploymentType -> deploymentType.getMappingsForFileSystems().equalsIgnoreCase(keys[finalI]))
                            .collect(Collectors.toList());

                    matchingDeploymentTypes.forEach(deploymentType ->
                            deploymentType.getReferenceDeployments().forEach(ref ->
                        fileSystem.getCapacities().put(ref.getName(), capacity)
                    ));

                }
                fileSystems.add(fileSystem);
            } catch (Exception ignore) {
            }
        });

        fileSystemsRepository.saveAll(fileSystems);

    }

}
