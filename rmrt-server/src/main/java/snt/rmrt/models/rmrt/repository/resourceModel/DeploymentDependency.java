
package snt.rmrt.models.rmrt.repository.resourceModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@IdClass(snt.rmrt.models.rmrt.IdClass.class)
public class DeploymentDependency implements Serializable {

    @Id private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;

    private String alias;
    private String groupId;
    private String artifactId;
    private String version;
    private String qualifier;

    @Column(name = "_order")
    private Integer order;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "deploymentDependency", fetch = FetchType.EAGER)
    private List<LoadConversionFormula> loadConversionFormulae = new ArrayList<>();

    public boolean containsLoadDriverValue(final List<String> values) {
        for(String value : values) {
            for (LoadConversionFormula ldc : loadConversionFormulae) {
                if (ldc.getFormula().equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean containsLoadDriverName(final List<String> names) {
        for(String name : names) {
            for (LoadConversionFormula ldc : loadConversionFormulae) {
                if (ldc.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> errors() {
        Set<String> errors = loadConversionFormulae.stream().map(LoadConversionFormula::getError)
                .collect(Collectors.toSet());
        errors.remove(null);
        return errors;
    }

    @JsonIgnore
    @ManyToOne
    private ResourceModel resourceModel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentDependency that = (DeploymentDependency) o;
        return owningElement.equals(that.owningElement) && name.equals(that.name) && isMaster.equals(that.isMaster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningElement, name, isMaster);
    }

    @Override
    public String toString() {
        return "DeploymentDependency{" +
                "owningElement='" + owningElement + '\'' +
                ", name='" + name + '\'' +
                ", isMaster=" + isMaster +
                ", alias='" + alias + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", order=" + order +
                ", loadConversionFormulae=" + loadConversionFormulae +
                '}';
    }
}