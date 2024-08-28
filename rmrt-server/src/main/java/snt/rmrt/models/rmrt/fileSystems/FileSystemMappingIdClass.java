package snt.rmrt.models.rmrt.fileSystems;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileSystemMappingIdClass implements Serializable {
    private String physicalMapping;
    private String cloudMapping;

    public FileSystemMappingIdClass() {}
    public FileSystemMappingIdClass(String physicalMapping, String cloudMapping) {
        this.physicalMapping = physicalMapping;
        this.cloudMapping = cloudMapping;
    }
}
