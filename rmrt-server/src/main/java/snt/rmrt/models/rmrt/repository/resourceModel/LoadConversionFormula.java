package snt.rmrt.models.rmrt.repository.resourceModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Entity
@Slf4j
@IdClass(snt.rmrt.models.rmrt.IdClass.class)
public class LoadConversionFormula implements Serializable {

    @Id private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;

    private Boolean isFormula = true;

    @Lob private String description;
    @Lob private String formula;
    @Lob private String parsedFormula;
    @Lob private String error;

    @Column(name = "_order")
    private Integer order;

    @ElementCollection
    @Column(name = "_values")
    private Map<String, Double> values = new HashMap<>();

    @JsonIgnore
    @ManyToOne
    private DeploymentDependency deploymentDependency;

    public String getParsedFormula() {
        if(this.parsedFormula == null) {
            this.parsedFormula = this.formula;
            // All words except all capitals -> removes excel function key words
            Pattern pattern = Pattern.compile("\\b[\\w]+\\b");
            AtomicReference<Matcher> matcher = new AtomicReference<>(pattern.matcher(this.parsedFormula));
            while (matcher.get().find()) {
                final String matchedString = matcher.get().group();
                this.deploymentDependency.getLoadConversionFormulae().stream()
                        .filter(ldc -> ldc.getName().equalsIgnoreCase(matchedString))
                        .forEach(ldc ->
                            updateLdcFormula(matcher, pattern, matchedString, ldc.getParsedFormula())
                        );
                this.deploymentDependency.getResourceModel().getDeploymentDependencies().stream()
                        .filter(dep -> !dep.equals(this.deploymentDependency))
                        .map(DeploymentDependency::getLoadConversionFormulae)
                        .flatMap(Collection::stream)
                        .filter(ldc -> ldc.getName().equalsIgnoreCase(matchedString))
                        .forEach(ldc ->
                            updateLdcFormula(matcher, pattern, matchedString, ldc.getParsedFormula())
                        );

                this.deploymentDependency.getResourceModel().getProperties().stream()
                        .filter(param -> param.getName().equalsIgnoreCase(matchedString))
                        .forEach(param ->
                            updateLdcFormula(matcher, pattern, matchedString, param.getDefaultValue().toString())
                        );
            }
        }
        return "("+this.parsedFormula+")";
    }

    private void updateLdcFormula(AtomicReference<Matcher> matcher, Pattern pattern, String matchedString, String value) {
        try {
            this.parsedFormula = this.parsedFormula.replaceAll("\\b"+matchedString+"\\b", value);
            matcher.set(pattern.matcher(this.parsedFormula));
        } catch (Exception e) {
            log.error("Error parsing ldc formula: "+this.name);
            log.error(e.getMessage());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadConversionFormula that = (LoadConversionFormula) o;
        return owningElement.equals(that.owningElement) && name.equals(that.name) && isMaster.equals(that.isMaster);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owningElement, name, isMaster);
    }

    @Override
    public String toString() {
        return "LoadConversionFormula{" +
                "owningElement='" + owningElement + '\'' +
                ", name='" + name + '\'' +
                ", isMaster=" + isMaster +
                ", description='" + description + '\'' +
                ", formula='" + formula + '\'' +
                ", error='" + error + '\'' +
                ", order=" + order +
                ", values=" + values +
                '}';
    }
}

