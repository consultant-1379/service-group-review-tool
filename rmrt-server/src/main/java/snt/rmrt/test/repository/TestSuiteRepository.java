package snt.rmrt.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import snt.rmrt.test.model.TestSuite;

public interface TestSuiteRepository extends JpaRepository<TestSuite, Integer>{

}
