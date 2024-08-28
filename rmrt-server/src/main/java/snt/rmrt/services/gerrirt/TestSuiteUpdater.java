package snt.rmrt.services.gerrirt;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.DeploymentResults;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.DisplayValue;
import snt.rmrt.models.readyReckoner.readyReckonerOutput.Results;
import snt.rmrt.test.model.TestSuite;
import snt.rmrt.test.repository.TestSuiteRepository;
import snt.rmrt.services.ReadyReckonerCalculation;

@Slf4j
@Service
public class TestSuiteUpdater {

	private final ReadyReckonerCalculation readyReckoner;
	private final TestSuiteRepository testSuiteRepo;
	static int failCount = 0;

	@Autowired
	public TestSuiteUpdater(ReadyReckonerCalculation readyReckoner, TestSuiteRepository testSuiteRepo) {
		this.readyReckoner = readyReckoner;
		this.testSuiteRepo = testSuiteRepo;
	}

	// Every 24 hours
	@Scheduled(fixedRate = 60000 * 60 * 24)
	private void sanityCheckUpdate() {
		failCount = 0;
		long startTime = System.currentTimeMillis();
		long endTime = 0;
		log.info("TestSuite Functionaity Started");
		try {
			sanityCheck();
		} catch (Exception exception) {
			log.error("SanityCheckFunctionality " + exception.getMessage());
		}
		if (failCount > 0) {
			log.info("Total number of testcases failed in TestSuite = {}", failCount);

		}
		log.info("TestSuite Functionaity Completed!!!!");
		endTime = System.currentTimeMillis();
		long totalMinutes = (endTime - startTime) / (1000 * 60);
		log.info("Total time taken by TestSuite = {} minutes.", totalMinutes);
	}

	private void sanityCheck() throws Exception {
		Map<String, Integer> keyValueMap = new LinkedHashMap<>();
		List<TestSuite> testCases = testSuiteRepo.findAll();
		for (TestSuite testcase : testCases) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				JsonNode jsonNodeArray = objectMapper.readTree(testcase.getNeTypeAndNodes());
				if (jsonNodeArray.isArray()) {
					for (JsonNode jsonNode : jsonNodeArray) {
						jsonNode.fields().forEachRemaining(entry -> {
							String key = entry.getKey();
							Integer value = entry.getValue().asInt();
							keyValueMap.put(key, value);
						});
					}
				}
			}	catch (Exception e) {
				e.getStackTrace();
				log.info("Something Wrong in Netype and value");
			}

			Results sanityResult = readyReckoner.evaluate(keyValueMap);
			keyValueMap.clear();
			List<DeploymentResults> sanitySupported = sanityResult.getSupported();
			if (testcase.getMediationComonentType().equalsIgnoreCase("No"))
			{
				boolean matchWithDeploy = sanitySupported.stream()
						.anyMatch(x -> x.getName().equalsIgnoreCase(testcase.getSmallestDeploymentType()));
				if (matchWithDeploy) {
					log.info("NonMediationType-> TestCaseNo::{} is passed of {}", testcase.getTestCaseNo(),
							testcase.getCapacityIdentifier());

				} else {
					failCount++;
					List<DeploymentResults> unSupported = sanityResult.getNotSupported();
					List<DeploymentResults> deploymentType = unSupported.stream()
							.filter(x -> x.getName().equalsIgnoreCase(testcase.getSmallestDeploymentType()))
							.collect(Collectors.toList());

					if (!deploymentType.isEmpty()) {
						List<DisplayValue> keyDimentionValues = deploymentType.get(0).getKeyDimensioningValues();
						List<DisplayValue> mediationComponentValues = deploymentType.get(0).getMediationComponents();
						StringBuilder failureReason = new StringBuilder();
						for (DisplayValue keyDimvalue : keyDimentionValues) {
							if (keyDimvalue.getValue() > keyDimvalue.getLimit()) {
								failureReason.append(String.format("KeyDimentioning Values :: %s -> %s/%s\n",
										keyDimvalue.getName(), keyDimvalue.getValue(), keyDimvalue.getLimit()));
							}
						}

						for (DisplayValue mediationValue : mediationComponentValues) {
							if (mediationValue.getValue() > mediationValue.getLimit()) {
								failureReason.append(String.format("MediationComponent Values :: %s -> %s/%s\n",
										mediationValue.getName(), mediationValue.getValue(),
										mediationValue.getLimit()));
							}
						}

						if (failureReason.length() > 0) {
							log.info(
									"NonMediationType-> TestCaseNo::{} is failed<==>{} <==> {} <==> {}, \n Failure reason -> {} ",
									testcase.getTestCaseNo(), testcase.getSmallestDeploymentType(),
									testcase.getCapacityIdentifier(), testcase.getNeTypeAndNodes(),
									failureReason.toString());
						} else {
							log.info("NonMediationType-> TestCaseNo::{} is failed<==>{}<==>{}<==>{} ",
									testcase.getTestCaseNo(), testcase.getSmallestDeploymentType(),
									testcase.getCapacityIdentifier(), testcase.getNeTypeAndNodes());
						}
					} else {
						log.info("NonMediationType-> TestCaseNo::{} is failed<==>{}<==>{}<==>{} ",
								testcase.getTestCaseNo(), testcase.getSmallestDeploymentType(),
								testcase.getCapacityIdentifier(), testcase.getNeTypeAndNodes());
					}

				}

			} else {
				for (DeploymentResults sanitySupportedElement : sanitySupported) {
					List<DisplayValue> mediationComponentsList = sanitySupportedElement.getMediationComponents();
					boolean anyMatchMed = mediationComponentsList.stream()
							.filter(x -> x.getName().equalsIgnoreCase(testcase.getMediationComonentType()))
							.anyMatch(x -> x.getValue() == 1.0);
					if (anyMatchMed) {
						log.info("   MediationType-> TestCaseNo::{} is passed of {}", testcase.getTestCaseNo(),
								testcase.getCapacityIdentifier());
						break;
					} else {
						failCount++;
						log.info("   MediationType-> TestCaseNo::{} is failed<==>{}<==>{}<==>{} ",
								testcase.getTestCaseNo(), testcase.getSmallestDeploymentType(),
								testcase.getCapacityIdentifier(), testcase.getNeTypeAndNodes());
						break;

					}
				}

			}
		}
	}
}

