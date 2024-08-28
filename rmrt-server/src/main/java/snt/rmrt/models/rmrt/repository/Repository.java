package snt.rmrt.models.rmrt.repository;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMapping;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
public class Repository implements Serializable {

    @Id
    private String project;
    private String filePath;
    private String branch = "master";
    private String name;

    private Boolean mediationComponent = false;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ResourceModel resourceModel;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "repository")
    private List<ChangeRequest> changeRequests = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<FileSystemMapping> fileSystemMappings = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<KeyDimensioningValue> keyDimensioningValues = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, Integer> instanceLimits = new HashMap<>();

    public void setResourceModel(ResourceModel resourceModel) {
        resourceModel.setRepository(this);
        this.resourceModel = resourceModel;
    }

    public String getName() {
        if(name == null || name.isEmpty()) {
            String[] a = project.split("/");
            name = a[a.length-1];
            if(name.equalsIgnoreCase("service-group-review-tool")) {
                name = "ENM Network Size";
            }
        }
        return name;
    }

}

