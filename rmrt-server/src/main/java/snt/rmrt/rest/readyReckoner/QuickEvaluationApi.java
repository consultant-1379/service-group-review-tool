package snt.rmrt.rest.readyReckoner;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.services.evaluator.QuickEvalEvaluator;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/quickEval")
public class QuickEvaluationApi {

    private final QuickEvalEvaluator quickEvalEvaluator;

    @Autowired
    public QuickEvaluationApi(QuickEvalEvaluator quickEvalEvaluator) {
        this.quickEvalEvaluator = quickEvalEvaluator;
    }

    @PostMapping
    public Map<String, Double> searchByName(@RequestBody Holder holder) {
        return quickEvalEvaluator.quickEval(holder.getOutputs(), holder.getInputs());
    }
}

@Data
class Holder {
    private List<String> outputs;
    private Map<String, Integer> inputs;
}