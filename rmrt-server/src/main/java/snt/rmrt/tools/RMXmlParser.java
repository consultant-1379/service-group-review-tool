package snt.rmrt.tools;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import snt.rmrt.models.rmrt.repository.resourceModel.*;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.ScaleUnit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Unit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Units;
import snt.rmrt.tools.lineNumberParser.DocumentFactoryWithLocator;
import snt.rmrt.tools.lineNumberParser.ElementWithLineNumber;
import snt.rmrt.tools.lineNumberParser.PrettySAXReader;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RMXmlParser {

    final private ResourceModel resourceModel = new ResourceModel();

    public RMXmlParser(File file, String name, String owningElement, boolean isMaster) {
        Locator locator = new LocatorImpl();
        DocumentFactory docFactory = new DocumentFactoryWithLocator(locator);
        SAXReader reader = new PrettySAXReader(docFactory, locator);

        resourceModel.setName(name);
        resourceModel.setOwningElement(owningElement);
        resourceModel.setIsMaster(isMaster);

        try {
            Document doc = reader.read(file);
            Element configEntity = doc.getRootElement();

            if (configEntity.getName().equalsIgnoreCase("configEntity")) {
                resourceModel.setValidSchema(true);
            }

            final String scaleUnitTag = "scaleUnit", parametersTag = "parameters", loadDriversTag = "loadDrivers",
                    deploymentDependenciesTag = "deploymentDependencies", descriptionTag = "description",
                    singletonTag = "singleton";

            for (Element element : configEntity.elements()) {
                final String elmName = element.getName();
                switch (elmName) {
                    case descriptionTag:
                        resourceModel.setDescription(element.getText());
                        break;
                    case singletonTag:
                        resourceModel.setSingleton(element.getText());
                        break;
                    case scaleUnitTag:
                        resourceModel.setScaleUnit(getScaleUnit(element));
                        Element loadDriverElement = element.element(loadDriversTag);
                        if (loadDriverElement == null) {
                            addErrorMessage("LoadDrivers tag missing from ScaleUnits.");
                        } else {
                            resourceModel.setLoadDrivers(getLoadDrivers(loadDriverElement));
                        }
                        break;
                    case parametersTag:
                        resourceModel.setProperties(getParameters(configEntity.element(parametersTag)));
                        break;
                    case deploymentDependenciesTag:
                        resourceModel.setDeploymentDependencies(getDeploymentDependencies(configEntity.element(deploymentDependenciesTag)));
                        break;
                    default:
                        addErrorMessage("XML Tag \"" + elmName + "\" is not recognised");
                }
            }

        } catch (DocumentException e) {
            //TODO
//            resourceModel.setErrorMessages(new HashSet<>(Collections.singletonList("Unable to read file")));
        }
    }

    public ResourceModel getResourceModel() {
        return resourceModel;
    }

    private ScaleUnit getScaleUnit(Element xmlScaleUnit) {
        ScaleUnit scaleUnit = new ScaleUnit();

        final String minScaleUnitTag = "minimumUnit", optScaleUnitTag = "optimalUnit";

        try {
            Element xmlMinScaleUnit = xmlScaleUnit.element(minScaleUnitTag);
            Units minimumUnits = getUnits(xmlMinScaleUnit);
            scaleUnit.setMinimumUnit(minimumUnits);
        } catch (Exception e) {
            if(e instanceof NullPointerException) {
                addErrorMessage("Error reading "+ minScaleUnitTag);
            } else {
                addErrorMessage(e.getClass().getName() + ":: " + e.getLocalizedMessage());
            }
        }

        try {
            ElementWithLineNumber xmlOptScaleUnit = (ElementWithLineNumber) xmlScaleUnit.element(optScaleUnitTag);
            Units optimalUnits = getUnits(xmlOptScaleUnit);
            scaleUnit.setOptimalUnit(optimalUnits);
        } catch (Exception e) {
            if(e instanceof NullPointerException) {
                addErrorMessage("Error reading "+ optScaleUnitTag);
            } else {
                addErrorMessage(e.getClass().getName() + ":: " + e.getLocalizedMessage());
            }
        }

        return scaleUnit;
    }

    private Units getUnits(Element xmlUnits) {
        Units units = new Units();
        String profileTag = "profile", conversionTag = "profileConversionFormulae";

        Element xmlProfile = xmlUnits.element(profileTag);
        Element xmlConversion = xmlUnits.element(conversionTag);

        units.setProfile(getUnit(xmlProfile));
        units.setConversion(getUnit(xmlConversion));

        return units;
    }

    private Unit getUnit(Element xmlProfile) {
        Unit unit = new Unit();
        String cpuCoresTag = "cpuCores", cpuMinutesTag = "cpuMinutes",
                peakCpuMinutesTag = "peakCpuMinutes", memoryTag = "memory";

        unit.setCpuCores(removeInvalidCharacters(xmlProfile.elementText(cpuCoresTag)));
        unit.setCpuMinutes(removeInvalidCharacters(xmlProfile.elementText(cpuMinutesTag)));
        unit.setPeakCpuMinutes(removeInvalidCharacters(xmlProfile.elementText(peakCpuMinutesTag)));
        unit.setMemory(removeInvalidCharacters(xmlProfile.elementText(memoryTag)));

        return unit;
    }

    private List<LoadDriver> getLoadDrivers(Element xmlLoadDrivers) {
        List<LoadDriver> loadDrivers = new ArrayList<>();
        final String nameAttr = "name", descriptionAttr = "description";
        int order = 0;
        for (Element xmlLoadDriver : xmlLoadDrivers.elements()) {
            if (xmlLoadDriver instanceof ElementWithLineNumber) {
                try {
                    LoadDriver loadDriver = new LoadDriver();

                    loadDriver.setOwningElement(resourceModel.getOwningElement());
                    loadDriver.setIsMaster(resourceModel.getIsMaster());
                    loadDriver.setName(removeInvalidCharacters(xmlLoadDriver.attributeValue(nameAttr)));
                    loadDriver.setResourceModel(resourceModel);

                    loadDriver.setOrder(order++);
                    loadDriver.setDescription(xmlLoadDriver.attributeValue(descriptionAttr));

                    //TODO better validation???
                    loadDriver.validate();
                    loadDrivers.add(loadDriver);
                } catch (ValidationException e) {
                    addErrorMessage(xmlLoadDriver, e.getMessage());
                }
            }
        }
        return loadDrivers;
    }

    private List<Property> getParameters(Element xmlParameters) {
        List<Property> properties = new ArrayList<>();
        try {
            String propertyTag = "property", nameTag = "name", defaultValueTag = "defaultValue", descriptionTag = "description";
            List<Element> xmlProperties = xmlParameters.elements(propertyTag);
            int order = 0;
            for (Element xmlProperty : xmlProperties) {
                if (xmlProperty instanceof ElementWithLineNumber) {
                    Property property = new Property();
                    try {
                        property.setOwningElement(resourceModel.getOwningElement());
                        property.setIsMaster(resourceModel.getIsMaster());
                        property.setName(removeInvalidCharacters(xmlProperty.attributeValue(nameTag)));
                        property.setResourceModel(resourceModel);

                        property.setOrder(order++);
                        property.setDescription(xmlProperty.attributeValue(descriptionTag));
                        try {
                            Double defaultValue = Double.parseDouble(xmlProperty.attributeValue(defaultValueTag));
                            property.setDefaultValue(defaultValue);
                        } catch (NumberFormatException e) {
                            addErrorMessage(xmlProperty, e.getMessage());
                        }
                    } catch (NullPointerException e) {
                        addErrorMessage(xmlProperty, "NullPointerException");
                    }
                    //TODO add validation method
                    properties.add(property);
                }
            }
        } catch (Exception e) {
            addErrorMessage(xmlParameters, "Properties::Exception: " + e.getLocalizedMessage());
        }
        return properties;
    }

    private List<DeploymentDependency> getDeploymentDependencies(Element xmlDeploymentDependencies) {
        List<DeploymentDependency> deploymentDependencies = new ArrayList<>();
        final String deploymentDependenciesTag = "deploymentDependency";
        try {

            int depOrder = 0;
            for (Element xmlDeploymentDependency : xmlDeploymentDependencies.elements(deploymentDependenciesTag)) {
                String aliasTag = "alias", groupIdTag = "groupId", artifactIdTag = "artifactId",
                        versionTag = "version", qualifierTag = "qualifier", loadDriverConvertersTag = "loadDriverConverters";

                DeploymentDependency deploymentDependency = new DeploymentDependency();

                deploymentDependency.setOwningElement(resourceModel.getOwningElement());
                deploymentDependency.setIsMaster(resourceModel.getIsMaster());
                deploymentDependency.setName(xmlDeploymentDependency.elementText(aliasTag));
                deploymentDependency.setResourceModel(resourceModel);

                deploymentDependency.setOrder(depOrder++);

                deploymentDependency.setAlias(xmlDeploymentDependency.elementText(aliasTag));
                deploymentDependency.setArtifactId(xmlDeploymentDependency.elementText(artifactIdTag));
                deploymentDependency.setGroupId(xmlDeploymentDependency.elementText(groupIdTag));
                deploymentDependency.setQualifier(xmlDeploymentDependency.elementText(qualifierTag));
                deploymentDependency.setVersion(xmlDeploymentDependency.elementText(versionTag));

                List<LoadConversionFormula> loadConversionFormulae = new ArrayList<>();
                Element xmlLoadConversionFormulae = xmlDeploymentDependency.element(loadDriverConvertersTag);

                final String loadConversionFormulaTag = "loadConversionFormula";
                int ldcOrder = 0;
                for (Element xmlLoadConversionFormula : xmlLoadConversionFormulae.elements(loadConversionFormulaTag)) {
                    try {
                        String nameAttr = "dependencyLoadDriver";

                        LoadConversionFormula loadConversionFormula = new LoadConversionFormula();

                        loadConversionFormula.setOwningElement(deploymentDependency.getOwningElement()+"-"+deploymentDependency.getName());
                        loadConversionFormula.setIsMaster(deploymentDependency.getIsMaster());
                        loadConversionFormula.setName(removeInvalidCharacters(xmlLoadConversionFormula.attributeValue(nameAttr)));
                        loadConversionFormula.setDeploymentDependency(deploymentDependency);

                        loadConversionFormula.setOrder(ldcOrder++);
                        loadConversionFormula.setFormula(removeInvalidCharacters(xmlLoadConversionFormula.getText()));

                        loadConversionFormulae.add(loadConversionFormula);
                    } catch (Exception e) {
                        if(e instanceof NullPointerException) {
                            addErrorMessage("Error reading "+ loadConversionFormulaTag);
                        } else {
                            addErrorMessage(e.getClass().getName() + ":: " + e.getLocalizedMessage());
                        }
                    }
                }
                deploymentDependency.setLoadConversionFormulae(loadConversionFormulae);
                deploymentDependencies.add(deploymentDependency);
            }
        } catch (Exception e) {
            if(e instanceof NullPointerException) {
                addErrorMessage("Error reading: " + deploymentDependenciesTag);
            } else {
                addErrorMessage(e.getClass().getName() + ":: " + e.getLocalizedMessage());
            }
        }
        return deploymentDependencies;
    }

    private String getLineAndColumnNumber(Element element) {
        return "Error found in XML at Line No: " + ((ElementWithLineNumber) element).getLineNumber() +
                ", Column No: " + ((ElementWithLineNumber) element).getColumnNumber() + ".\n";
    }

    //TODO change Error to Map<String, String>
    // Red, Orange, Yellow
    // Severe, Caution, Info
    private void addErrorMessage(Element element, String message) {
        resourceModel.getErrorMessages().add(getLineAndColumnNumber(element) + message);
    }

    private void addErrorMessage(String message) {
        resourceModel.getErrorMessages().add(message);
    }

    private String removeInvalidCharacters(String name) {
        name = name.replaceAll("[\\t\\n\\r ]", "").trim();
        if (name.isEmpty() || name.equals("()")) {
            name = "0";
        }
        return name;
    }

}