package snt.rmrt.rest.readyReckoner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.Results;
import snt.rmrt.services.ReadyReckonerCalculation;

import java.util.Map;

@RestController
@RequestMapping("api/readyReckoner")
@CrossOrigin
public class ReadyReckonerApi {

    private final ReadyReckonerCalculation readyReckoner;

    @Autowired
    public ReadyReckonerApi(ReadyReckonerCalculation readyReckoner) {
        this.readyReckoner = readyReckoner;
    }

    @PostMapping
    public Results postFromTool(@RequestBody Map<String, Integer> dataFromForm) {
        return readyReckoner.evaluate(dataFromForm);
    }

}

