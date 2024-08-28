package snt.rmrt.models.rmrt.referenceDeployment;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class ReferenceDeployment implements Serializable {

    @Id
    private String name;
    @Column(length = 5000)
    private String description;

    @ManyToOne
    private DeploymentType deploymentType = new DeploymentType();

}
