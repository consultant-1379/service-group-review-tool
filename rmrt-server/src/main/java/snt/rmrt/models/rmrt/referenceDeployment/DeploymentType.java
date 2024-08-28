package snt.rmrt.models.rmrt.referenceDeployment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
public class DeploymentType implements Serializable {

    @Id
    private String sizeKey;
    private String displayName;

    @Column(name = "_order")
    private Integer order;

    @Column(columnDefinition = "boolean default false")
    private Boolean visible;

    private String mappingsForFileSystems;

    @Transient
    private List<String> referenceDeploymentNames;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "deploymentType")
    private List<ReferenceDeployment> referenceDeployments;

    public List<String> getReferenceDeploymentNames() {
        return referenceDeployments.stream()
                .map(ReferenceDeployment::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "DeploymentType{" +
                "sizeKey='" + sizeKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", order=" + order + '\'' +
                ", referenceDeployments=" + getReferenceDeploymentNames() +
                '}';
    }
}
