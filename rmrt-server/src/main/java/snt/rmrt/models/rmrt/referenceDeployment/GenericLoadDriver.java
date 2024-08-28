package snt.rmrt.models.rmrt.referenceDeployment;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class GenericLoadDriver {

    @Id
    private String name;
    @Column(length = 5000)
    private String description;
    private Double value;
}