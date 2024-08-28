package snt.rmrt.models.readyReckoner.readyReckonerOutput;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Results implements Serializable {
    private Double totalNumberOfElements;

    private List<DeploymentResults> supported = new ArrayList<>();
    private List<DeploymentResults> notSupported = new ArrayList<>();

    private Map<String, String> errors = new HashMap<>();
}
