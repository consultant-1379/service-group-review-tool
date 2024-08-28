package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMapping;
import snt.rmrt.models.rmrt.fileSystems.FileSystemMappingIdClass;

@Repository
public interface FileSystemMappingsRepository extends JpaRepository<FileSystemMapping, FileSystemMappingIdClass> {
}
