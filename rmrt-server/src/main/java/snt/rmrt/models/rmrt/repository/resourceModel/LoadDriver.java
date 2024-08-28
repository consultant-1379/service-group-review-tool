package snt.rmrt.models.rmrt.repository.resourceModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.ValidationException;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@IdClass(snt.rmrt.models.rmrt.IdClass.class)
public class LoadDriver implements Serializable {

    @Id private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;

    @Column(name = "_order")
    private Integer order;

    @Column(length = 2000)
    private String description;

    public void validate() throws ValidationException {
        if(name == null || name.isEmpty()) {
            throw new ValidationException("Name cannot be null");
        }
    }

    @JsonIgnore
    @ManyToOne
    private ResourceModel resourceModel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadDriver that = (LoadDriver) o;
        return owningElement.equals(that.owningElement) && name.equals(that.name) && isMaster.equals(that.isMaster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningElement, name, isMaster);
    }

    @Override
    public String toString() {
        return "LoadDriver{" +
                "owningElement='" + owningElement + '\'' +
                ", name='" + name + '\'' +
                ", isMaster=" + isMaster +
                ", order=" + order +
                ", description='" + description + '\'' +
                '}';
    }
}
