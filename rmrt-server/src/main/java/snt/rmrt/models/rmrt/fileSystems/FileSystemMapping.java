package snt.rmrt.models.rmrt.fileSystems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadConversionFormula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@Entity
@IdClass(FileSystemMappingIdClass.class)
public class FileSystemMapping implements Serializable {

    @Id private String physicalMapping;
    @Id private String cloudMapping;

    @Column(length = 1000)
    private String customMappings;
    public List<String> getCustomMappings() {
        List<String> custom = new ArrayList<>();
        if(customMappings != null) {
            custom.addAll(Arrays.asList(customMappings.split(",")));
        }
        return custom;
    }
    public void setCustomMappings(Set<String> set) {
        customMappings = set.stream().reduce(",", String::concat);
    }

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name", name = "physicalMapping", insertable = false, updatable = false)
    private FileSystem physicalFileSystem;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "name", name = "cloudMapping", insertable = false, updatable = false)
    private FileSystem cloudFileSystem;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "fileSystemMappings")
    private Set<Repository> repositories = new HashSet<>();


    @Transient
    private Map<String, Double> totals = new HashMap<>();
    @Transient
    private Map<String, Double> capacities = new HashMap<>();
    @Transient
    private Map<String, Double> percentages = new HashMap<>();

    public Map<String, Double> getTotals() {
        if (totals == null || totals.isEmpty()) {
            totals = new HashMap<>();
            repositories.forEach(repo -> {
                repo.getResourceModel()
                        .getDeploymentDependencies().stream()
                        .map(DeploymentDependency::getLoadConversionFormulae)
                        .forEach(list -> {
                            Optional<LoadConversionFormula> nasfileSystemName = list.stream().filter(ldc -> ldc.getName().equalsIgnoreCase("nasfileSystemName")).findFirst();
                            Optional<LoadConversionFormula> nasFileSystemSpaceMB = list.stream().filter(ldc -> ldc.getName().equalsIgnoreCase("nasFileSystemSpaceMB")).findFirst();

                            nasfileSystemName.ifPresent(ldc -> {
                                if (allMappings().contains(ldc.getFormula())) {
                                    nasFileSystemSpaceMB.ifPresent(ldcVal -> {
                                        if (ldcVal.getError() == null) {
                                            ldcVal.getValues().forEach((key, val) -> {
                                                totals.compute(key, (k, v) -> v == null ? val : v + val);
                                            });
                                        }
                                    });
                                }
                            });
                        });
            });
        }
        return totals;
    }

    public Map<String, Double> getCapacities() {
        if ((capacities == null || capacities.isEmpty()) && physicalFileSystem != null && cloudFileSystem != null) {
            capacities = new HashMap<>();
            capacities.putAll(physicalFileSystem.getCapacities());
            capacities.putAll(cloudFileSystem.getCapacities());
        }
        return capacities;
    }

    public Map<String, Double> getPercentages() {
        if (percentages == null || percentages.isEmpty()) {
            percentages = new HashMap<>();
            getTotals().forEach((refName, value) ->
                percentages.put(refName, (value / getCapacities().getOrDefault(refName, 0.0)) * 100)
            );
        }
        return percentages;
    }

    public List<String> allMappings() {
        List<String> mappings = getCustomMappings();
        mappings.add(physicalMapping);
        mappings.add(cloudMapping);
        return mappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileSystemMapping that = (FileSystemMapping) o;
        return physicalMapping.equals(that.physicalMapping) && cloudMapping.equals(that.cloudMapping) && Objects.equals(customMappings, that.customMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(physicalMapping, cloudMapping, customMappings);
    }

    @Override
    public String toString() {
        return "FileSystems{" +
                "physicalMapping='" + physicalMapping + '\'' +
                ", cloudMapping='" + cloudMapping + '\'' +
                ", customMappings=" + customMappings +
                '}';
    }
}