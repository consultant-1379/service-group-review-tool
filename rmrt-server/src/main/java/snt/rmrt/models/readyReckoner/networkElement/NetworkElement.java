package snt.rmrt.models.readyReckoner.networkElement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import snt.rmrt.tools.NetworkElementDeserializer;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@JsonDeserialize(using = NetworkElementDeserializer.class)
public class NetworkElement extends NetworkElementDeserializer {

    @Id
    private String networkElementType;

    @Column(name = "_release")
    private Double release;
    private String domain;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @OrderBy("displayOrder")
    private Set<NEInput> inputs = new HashSet<>();

    @ElementCollection private Map<String, String> outputs;
    @ElementCollection private Map<String, String> inputPrePopulated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkElement that = (NetworkElement) o;
        return networkElementType.equals(that.networkElementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(networkElementType);
    }
}
