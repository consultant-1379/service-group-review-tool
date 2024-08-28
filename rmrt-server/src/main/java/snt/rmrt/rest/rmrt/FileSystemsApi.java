package snt.rmrt.rest.rmrt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.fileSystems.FileSystem;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMapping;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMappingIdClass;
import snt.rmrt.models.rmrt.fileSystems.FsType;
import snt.rmrt.repositories.FileSystemMappingsRepository;
import snt.rmrt.repositories.FileSystemsRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/fileSystems")
public class FileSystemsApi {

    private final FileSystemMappingsRepository fileSystemMappingsRepository;
    private final FileSystemsRepository fileSystemsRepository;

    @Autowired
    public FileSystemsApi(FileSystemMappingsRepository fileSystemMappingsRepository, FileSystemsRepository fileSystemsRepository) {
        this.fileSystemMappingsRepository = fileSystemMappingsRepository;
        this.fileSystemsRepository = fileSystemsRepository;
    }

    @GetMapping
    public List<FileSystemMapping> getFileSystemMappings() {
        return fileSystemMappingsRepository.findAll().stream()
                .sorted(Comparator
                        .comparing(FileSystemMapping::getPhysicalMapping, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(FileSystemMapping::getCloudMapping, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @GetMapping("names")
    public Map<String, List<String>> getFileSystemNames() {
        Map<String, List<String>> results = new HashMap<>();
        List<FileSystem> fileSystems = fileSystemsRepository.findAll().stream()
                .sorted(Comparator.comparing(FileSystem::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        results.put("cloud", fileSystems.stream()
                .filter(fileSystem -> fileSystem.getType().equals(FsType.CLOUD))
                .map(FileSystem::getName)
                .sorted()
                .collect(Collectors.toList()));
        results.put("physical", fileSystems.stream()
                .filter(fileSystem -> fileSystem.getType().equals(FsType.PHYSICAL))
                .map(FileSystem::getName)
                .sorted()
                .collect(Collectors.toList()));

        return results;
    }

    @PostMapping
    public FileSystemMapping createFileSystemMappings(@RequestBody FileSystemBaseInfo fileSystemBaseInfo) {
        FileSystemMapping fileSystemMapping = new FileSystemMapping();
        fileSystemMapping.setPhysicalMapping(fileSystemBaseInfo.getPhysicalMapping());
        fileSystemMapping.setCloudMapping(fileSystemBaseInfo.getCloudMapping());
        fileSystemMapping.setCustomMappings(fileSystemBaseInfo.getCustomMappings());

        return fileSystemMappingsRepository.save(fileSystemMapping);
    }

    @PutMapping
    public FileSystemMapping updateFileSystemMappings(@RequestBody FileSystemBaseInfo fileSystemBaseInfo) {
        FileSystemMapping fileSystemMapping = new FileSystemMapping();
        fileSystemMapping.setPhysicalMapping(fileSystemBaseInfo.getPhysicalMapping());
        fileSystemMapping.setCloudMapping(fileSystemBaseInfo.getCloudMapping());
        fileSystemMapping.setCustomMappings(fileSystemBaseInfo.getCustomMappings());

        return fileSystemMappingsRepository.save(fileSystemMapping);

    }

    @DeleteMapping
    public void deleteFileSystemMappings(@RequestParam String physicalMapping, @RequestParam String cloudMapping) {
        fileSystemMappingsRepository.deleteById(new FileSystemMappingIdClass(physicalMapping, cloudMapping));
    }

}

@Data
class FileSystemBaseInfo {
    private String physicalMapping;
    private String cloudMapping;
    private Set<String> customMappings;
}
