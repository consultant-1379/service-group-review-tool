package snt.rmrt.services.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.*;
import snt.rmrt.models.rmrt.referenceDeployment.DeploymentDependantLoadDriver;
import snt.rmrt.models.rmrt.referenceDeployment.ReferenceDeployment;
import snt.rmrt.models.rmrt.repository.resourceModel.DeploymentDependency;
import snt.rmrt.models.rmrt.repository.resourceModel.ResourceModel;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.ScaleUnit;
import snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class Evaluator {

    void evaluateResourceModel(Workbook workbook, List<ReferenceDeployment> referenceDeployments, List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers, ResourceModel resourceModel) {
        referenceDeployments.forEach(referenceDeployment -> {
            updateWorkbookNamedRanges(workbook, referenceDeployment, deploymentDependantLoadDrivers);
            evaluateForReferenceDeployment(workbook, referenceDeployment, resourceModel);
        });
    }
    void evaluateForReferenceDeployment(final Workbook workbook, final ReferenceDeployment referenceDeployment, final ResourceModel resourceModel) {

        Sheet evaluationSheet = workbook.createSheet();
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        // Must evaluate scaleUnit first to provide 'required_instances'
        final ScaleUnit scaleUnit = resourceModel.getScaleUnit();
        evaluateScaleUnits(scaleUnit.getMinimumUnit().getProfile(), referenceDeployment.getName(), evaluationSheet, evaluator);
        evaluateScaleUnits(scaleUnit.getMinimumUnit().getConversion(), referenceDeployment.getName(), evaluationSheet, evaluator);
        evaluateScaleUnits(scaleUnit.getOptimalUnit().getProfile(), referenceDeployment.getName(), evaluationSheet, evaluator);
        evaluateScaleUnits(scaleUnit.getOptimalUnit().getConversion(), referenceDeployment.getName(), evaluationSheet, evaluator);

        Map<String, Double> map = scaleUnit.getRequired_instances();
        Double requiredInstances = map.get(referenceDeployment.getName());

        Name name = workbook.getName("required_instances");
        name.setRefersToFormula(requiredInstances.isNaN() || requiredInstances.isInfinite() ? "0" : String.valueOf(requiredInstances));

        evaluateConversionFormulae(resourceModel.getDeploymentDependencies(), referenceDeployment.getName(), evaluationSheet, evaluator);
    }

    private void evaluateScaleUnits(final Unit profile, final String referenceDeploymentSheet, final Sheet evaluationSheet, final FormulaEvaluator evaluator) {

        // CPU Cores
        try {
            final Cell evaluationCell = evaluationSheet.createRow(0).createCell(0);
            evaluationCell.setCellFormula(profile.getParsedCpuCores());
            final CellValue result = evaluator.evaluate(evaluationCell);

            profile.getCpuCores_values().put(referenceDeploymentSheet, result.getNumberValue());
            profile.setCpuCores_error(null);
        } catch (Exception exception) {
            profile.setCpuCores_error(errorMessage(exception));
        }

        // CPU Minutes
        try {
            final Cell evaluationCell = evaluationSheet.getRow(0).createCell(1);
            evaluationCell.setCellFormula(profile.getParsedCpuMinutes());
            final CellValue result = evaluator.evaluate(evaluationCell);

            profile.getCpuMinutes_values().put(referenceDeploymentSheet, result.getNumberValue());
            profile.setCpuMinutes_error(null);
        } catch (FormulaParseException exception) {
            profile.setCpuMinutes_error(errorMessage(exception));
        }

        // Peak CPU Minutes
        try {
            final Cell evaluationCell = evaluationSheet.getRow(0).createCell(2);
            evaluationCell.setCellFormula(profile.getParsedPeakCpuMinutes());
            final CellValue result = evaluator.evaluate(evaluationCell);

            profile.getPeakCpuMinutes_values().put(referenceDeploymentSheet, result.getNumberValue());
            profile.setPeakCpuMinutes_error(null);
        } catch (Exception exception) {
            profile.setPeakCpuMinutes_error(errorMessage(exception));
        }

        // Memory
        try {
            final Cell evaluationCell = evaluationSheet.getRow(0).createCell(3);
            evaluationCell.setCellFormula(profile.getParsedMemory());
            final CellValue result = evaluator.evaluate(evaluationCell);

            profile.getMemory_values().put(referenceDeploymentSheet, result.getNumberValue());
            profile.setMemory_error(null);
        } catch (Exception exception) {
            profile.setMemory_error(errorMessage(exception));
        }

    }
    private void evaluateConversionFormulae(final List<DeploymentDependency> deploymentDependencies, final String referenceDeploymentSheet, final Sheet evaluationSheet, final FormulaEvaluator evaluator) {
        final AtomicInteger column = new AtomicInteger(0);
        evaluationSheet.createRow(0);
        deploymentDependencies.forEach(dependency ->
                dependency.getLoadConversionFormulae().forEach(loadConversionFormula -> {
                    try {
                        if (!(loadConversionFormula.getName().matches("^.*(Name|Name_.*|Type|Type_.*)$"))) {
                            final Cell evaluationCell = evaluationSheet.getRow(0).createCell(column.getAndIncrement());
                            evaluationCell.setCellFormula(loadConversionFormula.getParsedFormula());

                            final CellValue result = evaluator.evaluate(evaluationCell);
                            loadConversionFormula.getValues().put(referenceDeploymentSheet, result.getNumberValue());

                        } else {
                            loadConversionFormula.setIsFormula(false);
                        }

                    } catch (Exception exception) {
                        loadConversionFormula.setValues(null);
                        String message = errorMessage(exception);
                        loadConversionFormula.setError(message);
                    }
                })
        );
    }

    void updateWorkbookNamedRanges(Workbook workbook, ReferenceDeployment referenceDeployment, List<DeploymentDependantLoadDriver> deploymentDependantLoadDrivers) {

        List<Name> names = new ArrayList<>(workbook.getAllNames());
        names.forEach(workbook::removeName);

        final Name enm_deployment_type_Name = workbook.createName();
        enm_deployment_type_Name.setNameName("enm_deployment_type");
        enm_deployment_type_Name.setRefersToFormula("\""+referenceDeployment.getDeploymentType().getSizeKey()+"\"");

        final Name required_instances_Name = workbook.createName();
        required_instances_Name.setNameName("required_instances");
        required_instances_Name.setRefersToFormula("0");

        deploymentDependantLoadDrivers.forEach(depLd -> {
            try {
                final Name namedCell = workbook.createName();
                namedCell.setNameName(depLd.getName());
                namedCell.setRefersToFormula(depLd.getValues().getOrDefault(referenceDeployment.getName(), "0"));
            } catch (Exception e) {
                log.warn("Error creating named range for: " + depLd.getName() + ", with Reference Deployment: "+referenceDeployment.getName());
            }
        });
    }

    String errorMessage(Exception exception) {
        if(exception instanceof NullPointerException) {
            return "Null pointer exception while evaluating resource model.";
        }
        String message = exception.getMessage();

        if(message.contains("named range") || message.contains("current workbook")) {
            message = message
                    .replace("named range", "Load Driver")
                    .replace("current workbook", "database");
        }
        if(message.contains("RefListEval")) {
            message = "Error while evaluating formula. " +
                    "This may be caused by a misplaced comma, decimal point or parenthesis (\",\" or \".\")";
        }

        return message;

    }

}
