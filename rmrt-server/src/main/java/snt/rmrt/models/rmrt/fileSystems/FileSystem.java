package snt.rmrt.models.rmrt.fileSystems;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class FileSystem implements Serializable {

    @Id private String name;

    @Enumerated(EnumType.STRING)
    private FsType type;

    @ElementCollection(fetch =  FetchType.EAGER)
    private Map<String, Double> capacities = new HashMap<>();
}

