package snt.rmrt.models.readyReckoner.networkElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
public class NEInput {

    @Id
    private String inputKey;

    private String inputLabel;
    private int displayOrder;

    @ManyToMany(mappedBy = "inputs", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<NetworkElement> networkElement;

    public NEInput(String label, String inputKey, int displayOrder) {
        this.inputLabel = label;
        this.inputKey = inputKey;
        this.displayOrder = displayOrder;
    }

    public NEInput() {

    }
}
