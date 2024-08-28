package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import snt.rmrt.models.rmrt.repository.Repository;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, String> {
}
