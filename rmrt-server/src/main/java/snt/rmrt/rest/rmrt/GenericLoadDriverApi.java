package snt.rmrt.rest.rmrt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.models.rmrt.referenceDeployment.GenericLoadDriver;
import snt.rmrt.repositories.GenericLoadDriverRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/genericLoadDrivers")
public class GenericLoadDriverApi {

    private final GenericLoadDriverRepository genericLoadDriverRepository;

    @Autowired
    public GenericLoadDriverApi(GenericLoadDriverRepository genericLoadDriverRepository) {
        this.genericLoadDriverRepository = genericLoadDriverRepository;
    }

    @GetMapping
    public List<GenericLoadDriver> getGenericLoadDrivers() {
        return this.genericLoadDriverRepository.findAll().stream()
                .sorted(Comparator.comparing(GenericLoadDriver::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @PostMapping
    public GenericLoadDriver createGenericLoadDrivers(@RequestBody GenericLoadDriver genericLoadDriver) {
        return genericLoadDriverRepository.save(genericLoadDriver);
    }

    @PutMapping
    public GenericLoadDriver updateGenericLoadDrivers(@RequestBody GenericLoadDriver genericLoadDriver) {
        return genericLoadDriverRepository.save(genericLoadDriver);
    }

    @DeleteMapping
    public void deleteGenericLoadDrivers(@RequestParam String name) {
        genericLoadDriverRepository.deleteById(name);
    }

}
