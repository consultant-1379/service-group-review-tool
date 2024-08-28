package snt.rmrt.models.rmrt.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import snt.rmrt.models.rmrt.repository.resourceModel.LoadDriver;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class ChangeRequest implements Serializable {

    public ChangeRequest() {}

    public ChangeRequest(Repository repository) {
        this.repository = repository;
    }

    @Id
    private String changeId;
    private String revisionId;

    private String owner;
    private String status;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ResourceModel resourceModel;

    @OneToMany
    private Set<LoadDriver> newLoadDrivers = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    private Repository repository;

    public void setResourceModel(ResourceModel resourceModel) {
        resourceModel.setChangeRequest(this);
        this.resourceModel = resourceModel;
    }
}
