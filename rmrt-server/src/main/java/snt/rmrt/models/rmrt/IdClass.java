package snt.rmrt.models.rmrt;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class IdClass implements Serializable {
    @Id private String owningElement;
    @Id private String name;
    @Id private Boolean isMaster;


}
