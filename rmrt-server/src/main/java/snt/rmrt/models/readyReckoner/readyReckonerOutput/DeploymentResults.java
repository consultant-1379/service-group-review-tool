package snt.rmrt.models.readyReckoner.readyReckonerOutput;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeploymentResults implements Serializable {

    private String name;
    private String deploymentSize;

    private List<DisplayValue> mediationComponents = new ArrayList<>();
    private List<DisplayValue> keyDimensioningValues = new ArrayList<>();
}