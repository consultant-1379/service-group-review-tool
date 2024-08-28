package snt.rmrt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import snt.rmrt.models.readyReckoner.fsRelease.FSRelease;

public interface FSReleaseRepository extends JpaRepository<FSRelease, String> {
    FSRelease getByRelease(String release);
}
