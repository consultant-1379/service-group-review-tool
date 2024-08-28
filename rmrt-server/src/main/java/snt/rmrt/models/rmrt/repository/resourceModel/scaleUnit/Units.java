package snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Units implements Serializable {

    private Unit profile = new Unit();
    private Unit conversion = new Unit();

    public List<String> errors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(profile.errors());
        errors.addAll(conversion.errors());
        return errors;
    }
}
