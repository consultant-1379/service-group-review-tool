package snt.rmrt.models.readyReckoner.readyReckonerOutput;

import lombok.Data;

import java.io.Serializable;

@Data
public class DisplayValue implements Serializable {
    private String name;
    private Double value;
    private Double limit;
}

