package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.fileSystems.FileSystem;

@Repository
public interface FileSystemsRepository extends JpaRepository<FileSystem, String> {
}
