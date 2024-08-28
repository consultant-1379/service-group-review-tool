package snt.rmrt.models.rmrt.repository.resourceModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import snt.rmrt.models.rmrt.repository.ChangeRequest;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.ScaleUnit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@IdClass(snt.rmrt.models.rmrt.IdClass.class)
public class ResourceModel implements Serializable {

    @Id private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;

    @Column(length = 1000)
    private String description;
    private String singleton;

    @Lob
    private ScaleUnit scaleUnit = new ScaleUnit();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resourceModel")
    private List<LoadDriver> loadDrivers = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resourceModel")
    private List<Property> properties = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resourceModel")
    private List<DeploymentDependency> deploymentDependencies = new ArrayList<>();

    private boolean validSchema = false;

    @Transient
    public Set<String> getErrorMessages() {
        Set<String> errorMessages = new HashSet<>();
        errorMessages.addAll(scaleUnit.errors());
        errorMessages.addAll(deploymentDependencies.stream()
                .flatMap(dep -> dep.errors().stream())
                .collect(Collectors.toSet()));
        return errorMessages;
    }

    @JsonIgnore
    public Set<String> names() {
        Set<String> names = new HashSet<>();
        names.addAll(getProperties().stream().map(Property::getName).collect(Collectors.toList()));
        names.addAll(getLoadDrivers().stream().map(LoadDriver::getName).collect(Collectors.toList()));
        names.addAll(getDeploymentDependencies().stream()
                .map(DeploymentDependency::getLoadConversionFormulae)
                .flatMap(Collection::stream)
                .map(LoadConversionFormula::getName)
                .collect(Collectors.toList()));
        return names;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceModel that = (ResourceModel) o;
        return owningElement.equals(that.owningElement) && name.equals(that.name) && isMaster.equals(that.isMaster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningElement, name, isMaster);
    }

    @Override
    public String toString() {
        return "ResourceModel{" +
                "owningElement='" + owningElement + '\'' +
                ", name='" + name + '\'' +
                ", isMaster=" + isMaster +
                ", description='" + description + '\'' +
                ", singleton='" + singleton + '\'' +
                ", scaleUnit=" + scaleUnit +
                ", loadDrivers=" + loadDrivers +
                ", properties=" + properties +
                ", deploymentDependencies=" + deploymentDependencies +
                ", validSchema=" + validSchema +
                '}';
    }

    @JsonIgnore
    @OneToOne(mappedBy = "resourceModel")
    private Repository repository;

    @JsonIgnore
    @OneToOne(mappedBy = "resourceModel")
    private ChangeRequest changeRequest;

    public ChangeRequest getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
    }
}

