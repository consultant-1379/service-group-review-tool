package snt.rmrt.rest.rmrt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import snt.rmrt.services.gerrirt.RepositoryUpdater;
import snt.rmrt.models.rmrt.repository.Repository;
import snt.rmrt.repositories.RepositoryRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/repositories")
public class RepositoryApi {

    private final RepositoryRepository repositoryRepository;
    private final RepositoryUpdater repositoryUpdater;

    @Autowired
    public RepositoryApi(RepositoryRepository repositoryRepository, RepositoryUpdater repositoryUpdater) {
        this.repositoryRepository = repositoryRepository;
        this.repositoryUpdater = repositoryUpdater;
    }

    @GetMapping
    public List<Repository> getRepository(@RequestParam(required = false) String project) {
        List<Repository> repositories;
        if(project == null) {
            repositories = repositoryRepository.findAll().stream()
                    .sorted(Comparator.comparing(Repository::getName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } else {
            repositories = repositoryRepository.findAllById(Collections.singleton(project));
            repositories.forEach(repo -> {
                if(repo.getLastUpdated().isBefore(LocalDateTime.now().minusMinutes(10))) {
                    log.info("Updating Repository After Request: "+repo.getName());
                    repositoryUpdater.updateRepository(repo);
                }
            });
        }
        return repositories;
    }

    @PostMapping
    public Repository createRepository(@RequestBody Repository repository) {
        repository.setLastUpdated(LocalDateTime.now().minusMinutes(15));
        return repositoryRepository.save(repository);
    }

    @PutMapping
    public Repository updateRepository(@RequestBody Repository repository) {
        Optional<Repository> current = repositoryRepository.findById(repository.getProject());
        if(current.isPresent()) {

            current.get().setName(repository.getName());
            current.get().setFilePath(repository.getFilePath());
            current.get().setMediationComponent(repository.getMediationComponent());
            current.get().setInstanceLimits(repository.getInstanceLimits());

            return repositoryRepository.save(current.get());
        } else {
            return repositoryRepository.save(repository);
        }
    }

    @DeleteMapping
    public void deleteRepository(@RequestParam String project) {
        repositoryRepository.deleteById(project);
    }


}
