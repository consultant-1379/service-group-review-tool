package snt.rmrt.models.rmrt.keyDimensioning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Data
@Entity
public class KeyDimensioningValue implements Serializable {
    @Id private String name;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, Double> capacities = new HashMap<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, Double> capacitiesPerDeploymentTypes = new HashMap<>();

    @Transient private Map<String, Double> totals = new HashMap<>();
    @Transient private Map<String, Double> percentages = new HashMap<>();

    public Map<String, Double> getTotals() {
        if(totals == null || totals.isEmpty()) {
            totals = new HashMap<>();
            getRepositories().forEach(repo -> {
                repo.getResourceModel()
                        .getDeploymentDependencies().stream()
                        .map(DeploymentDependency::getLoadConversionFormulae)
                        .flatMap(Collection::stream)
                        .forEach(ldc -> {
                            if(ldc.getName().equalsIgnoreCase(name) && ldc.getError() == null) {
                                ldc.getValues().forEach((key, val) ->
                                        totals.compute(key, (k, v) -> v==null ? val : v + val)
                                );
                            }
                        });
            });
        }
        return totals;
    }
    public Map<String, Double> getPercentages() {
        if(percentages == null || percentages.isEmpty()) {
            percentages = new HashMap<>();
            getTotals().forEach((refName, value) ->
                    percentages.put(refName, (value / getCapacities().getOrDefault(refName, 0.0)) * 100));
        }
        return percentages;
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "keyDimensioningValues")
    private Set<Repository> repositories = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyDimensioningValue that = (KeyDimensioningValue) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "KeyDimensioningValue{" +
                "name='" + name + '\'' +
                ", capacities=" + capacities +
                '}';
    }

}
