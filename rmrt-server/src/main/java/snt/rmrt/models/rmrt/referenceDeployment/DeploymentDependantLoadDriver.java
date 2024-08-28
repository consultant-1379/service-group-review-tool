package snt.rmrt.models.rmrt.referenceDeployment;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;
import java.util.TreeMap;

@Data
@Entity
public class DeploymentDependantLoadDriver {

    @Id
    private String name;
    @Column(length = 5000)
    private String description;

    @Column(name = "_values")
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> values = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}
