package snt.rmrt.test.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Entity

public class TestSuite implements Serializable {

	@Id
	private Integer testCaseNo;
	private String smallestDeploymentType;
	private String capacityIdentifier;
	private String mediationComonentType;
	private String neTypeAndNodes;

}