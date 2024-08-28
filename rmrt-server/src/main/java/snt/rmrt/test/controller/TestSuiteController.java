package snt.rmrt.test.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaTypeEditor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import snt.rmrt.test.model.TestSuite;
import snt.rmrt.test.repository.TestSuiteRepository;

@RestController
@RequestMapping("api/testCases")
public class TestSuiteController {

	private final TestSuiteRepository testSuiteRepository;

	@Autowired
	public TestSuiteController(TestSuiteRepository testSuiteRepository) {
		this.testSuiteRepository = testSuiteRepository;
	}

	@GetMapping("/getalltestcases")
	public List<TestSuite> getAllTestCases() {
		return testSuiteRepository.findAll().stream().collect(Collectors.toList());
	}

	@PostMapping("/addTestCase")
	public String createTestCase(@RequestBody TestSuite testcase) {
		List<TestSuite> allTestCases = testSuiteRepository.findAll();
		if (allTestCases.isEmpty()) {
			testSuiteRepository.save(testcase);
			return " TestCase Created";

		} else {
			List<TestSuite> relatedTestCase = allTestCases.stream()
					.filter(x -> x.getSmallestDeploymentType().equalsIgnoreCase(testcase.getSmallestDeploymentType()))
					.collect(Collectors.toList());
			boolean matchWithNe = relatedTestCase.stream()
					.anyMatch(x -> x.getNeTypeAndNodes().equalsIgnoreCase(testcase.getNeTypeAndNodes())
							&& x.getMediationComonentType().equalsIgnoreCase(testcase.getMediationComonentType())
							&& x.getSmallestDeploymentType().equalsIgnoreCase(testcase.getSmallestDeploymentType()));

			if (matchWithNe) {
				return "Test Case Already exists";
			} else {
				testSuiteRepository.save(testcase);
				return testcase.getCapacityIdentifier() + " with " + testcase.getSmallestDeploymentType()
						+ " testcase is sucessfully added";

			}

		}
	}

	@PutMapping("/{testCaseNo}")
	public String updateTestCase(@PathVariable Integer testCaseNo, @RequestBody TestSuite testcase) {
		Optional<TestSuite> findByIdTest = testSuiteRepository.findById(testCaseNo);
		if (findByIdTest.isPresent()) {
			TestSuite TestCaseToBeUpdated = findByIdTest.get();
			TestCaseToBeUpdated.setCapacityIdentifier(testcase.getCapacityIdentifier());
			TestCaseToBeUpdated.setSmallestDeploymentType(testcase.getSmallestDeploymentType());
			TestCaseToBeUpdated.setMediationComonentType(testcase.getMediationComonentType());
			TestCaseToBeUpdated.setNeTypeAndNodes(testcase.getNeTypeAndNodes());
			testSuiteRepository.save(TestCaseToBeUpdated);
			return testcase.getTestCaseNo() + "  is updated";
		} else {
			return testcase.getTestCaseNo() + " not found !";
		}
	}

	@PatchMapping("/{testCaseNo}")
	public String partialUpdate(@PathVariable Integer testCaseNo, @RequestBody TestSuite testcase) {

		Optional<TestSuite> findByIdTest = testSuiteRepository.findById(testCaseNo);
		if (findByIdTest.isPresent()) {
			TestSuite TestCaseToBeUpdated = findByIdTest.get();
			TestCaseToBeUpdated.setNeTypeAndNodes(testcase.getNeTypeAndNodes());
			testSuiteRepository.save(TestCaseToBeUpdated);
			return testcase.getTestCaseNo() + "  is updated";
		} else {
			return testcase.getTestCaseNo() + " not found !";
		}
	}

	@DeleteMapping("/{testCaseNo}")
	public String deleteTestCase(@PathVariable("testCaseNo") Integer testCaseNo, TestSuite testcase) {
		Optional<TestSuite> findByIdTest = testSuiteRepository.findById(testCaseNo);
		if (findByIdTest.isPresent()) {
			testSuiteRepository.deleteById(testCaseNo);
			return testcase.getTestCaseNo() + " is deleted sucessfully";
		} else {
			return testcase.getTestCaseNo() + " not found";

		}
	}
}