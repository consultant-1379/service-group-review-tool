package snt.rmrt.rest.rmrt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import snt.rmrt.models.rmrt.keyDimensioning.KeyDimensioningValue;
import snt.rmrt.repositories.KeyDimensioningValuesRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/keyDimensioningValues")
public class KeyDimensioningValuesApi {

    private final KeyDimensioningValuesRepository keyDimensioningValuesRepository;

    @Autowired
    public KeyDimensioningValuesApi(KeyDimensioningValuesRepository keyDimensioningValuesRepository) {
        this.keyDimensioningValuesRepository = keyDimensioningValuesRepository;
    }

    @GetMapping
    public List<KeyDimensioningValue> getKeyDimensioningValues() {
        return keyDimensioningValuesRepository.findAll().stream()
                .sorted(Comparator.comparing(KeyDimensioningValue::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

}
