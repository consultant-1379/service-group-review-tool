package snt.rmrt.models.rmrt.repository.resourceModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@IdClass(snt.rmrt.models.rmrt.IdClass.class)
public class Property implements Serializable {

    @Id
    private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;

    @Column(name = "_order")
    private Integer order;

    @Column(length = 1000)
    private String description;
    private Double defaultValue = 0.0;

    @JsonIgnore
    @ManyToOne
    private ResourceModel resourceModel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return owningElement.equals(property.owningElement) && name.equals(property.name) && isMaster.equals(property.isMaster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningElement, name, isMaster);
    }

    @Override
    public String toString() {
        return "Property{" +
                "owningElement='" + owningElement + '\'' +
                ", name='" + name + '\'' +
                ", isMaster=" + isMaster +
                ", order=" + order +
                ", description='" + description + '\'' +
                ", defaultValue=" + defaultValue +
                '}';
    }
}