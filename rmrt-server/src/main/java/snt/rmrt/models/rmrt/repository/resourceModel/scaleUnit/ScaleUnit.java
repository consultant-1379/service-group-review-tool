package snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class ScaleUnit implements Serializable {

    private Units minimumUnit = new Units();
    private Units optimalUnit = new Units();

    private Map<String, Double> cpuCores_instances = new HashMap<>();
    private Map<String, Double> memory_instances = new HashMap<>();
    private Map<String, Double> cpuMinutes_instances = new HashMap<>();
    private Map<String, Double> peakCpuMinutes_instances = new HashMap<>();

    private Map<String, Double> required_instances = new HashMap<>();

    // ((OptimalConversion-MinimumConversion) / ( OptimalProfile-MinimumConversion))
    private Double calculate(Double optimalConversion, Double minimumConversion, Double optimalProfile) {
        try {
            final double usedPeakCpuMins = optimalConversion - minimumConversion;
            final double usablePeakCpuMinutes = optimalProfile - minimumConversion;
            final double result = Math.ceil(usedPeakCpuMins / usablePeakCpuMinutes);
            return Double.isNaN(result) ? 0.0 : result;
        } catch (Exception exception) {
            return Double.NaN;
        }
    }

    public Map<String, Double> getCpuCores_instances() {
        Set<String> referenceDeployments = new HashSet<>();
        referenceDeployments.addAll(optimalUnit.getConversion().getCpuCores_values().keySet());
        referenceDeployments.addAll(minimumUnit.getConversion().getCpuCores_values().keySet());
        referenceDeployments.forEach(referenceDeployment -> {
            final Double optimalConversionVal = optimalUnit.getConversion().getCpuCores_values().get(referenceDeployment);
            final Double minimumConversionVal = minimumUnit.getConversion().getCpuCores_values().get(referenceDeployment);
            final Double optimalProfileVal = optimalUnit.getProfile().getCpuCores_values().get(referenceDeployment);

            double result = calculate(optimalConversionVal, minimumConversionVal, optimalProfileVal);

            cpuCores_instances.put(referenceDeployment, result);
        });
        return cpuCores_instances;
    }
    public Map<String, Double> getCpuMinutes_instances() {
        Set<String> referenceDeployments = new HashSet<>();
        referenceDeployments.addAll(optimalUnit.getConversion().getCpuMinutes_values().keySet());
        referenceDeployments.addAll(minimumUnit.getConversion().getCpuMinutes_values().keySet());
        referenceDeployments.forEach(referenceDeployment -> {
            final Double optimalConversionVal = optimalUnit.getConversion().getCpuMinutes_values().get(referenceDeployment);
            final Double minimumConversionVal = minimumUnit.getConversion().getCpuMinutes_values().get(referenceDeployment);
            final Double optimalProfileVal = optimalUnit.getProfile().getCpuMinutes_values().get(referenceDeployment);

            double result = calculate(optimalConversionVal, minimumConversionVal, optimalProfileVal);

            cpuMinutes_instances.put(referenceDeployment, result);
        });
        return cpuMinutes_instances;
    }
    public Map<String, Double> getMemory_instances() {
        Set<String> referenceDeployments = new HashSet<>();
        referenceDeployments.addAll(optimalUnit.getConversion().getMemory_values().keySet());
        referenceDeployments.addAll(minimumUnit.getConversion().getMemory_values().keySet());
        referenceDeployments.forEach(referenceDeployment -> {
            final Double optimalConversionVal = optimalUnit.getConversion().getMemory_values().get(referenceDeployment);
            final Double minimumConversionVal = minimumUnit.getConversion().getMemory_values().get(referenceDeployment);
            final Double optimalProfileVal = optimalUnit.getProfile().getMemory_values().get(referenceDeployment);

            double result = calculate(optimalConversionVal, minimumConversionVal, optimalProfileVal);

            memory_instances.put(referenceDeployment, result);
        });
        return memory_instances;
    }
    public Map<String, Double> getPeakCpuMinutes_instances() {
        Set<String> referenceDeployments = new HashSet<>();
        referenceDeployments.addAll(optimalUnit.getConversion().getPeakCpuMinutes_values().keySet());
        referenceDeployments.addAll(minimumUnit.getConversion().getPeakCpuMinutes_values().keySet());
        referenceDeployments.forEach(referenceDeployment -> {
            final Double optimalConversionVal = optimalUnit.getConversion().getPeakCpuMinutes_values().get(referenceDeployment);
            final Double minimumConversionVal = minimumUnit.getConversion().getPeakCpuMinutes_values().get(referenceDeployment);
            final Double optimalProfileVal = optimalUnit.getProfile().getPeakCpuMinutes_values().get(referenceDeployment);

            double result = calculate(optimalConversionVal, minimumConversionVal, optimalProfileVal);

            peakCpuMinutes_instances.put(referenceDeployment, result);
        });
        return peakCpuMinutes_instances;
    }

    public Map<String, Double> getRequired_instances() {
        Set<String> referenceDeployments = new HashSet<>();
        referenceDeployments.addAll(getCpuCores_instances().keySet());
        referenceDeployments.addAll(getMemory_instances().keySet());
        referenceDeployments.addAll(getCpuMinutes_instances().keySet());
        referenceDeployments.addAll(getPeakCpuMinutes_instances().keySet());

        referenceDeployments.forEach(referenceDeployment -> {
            final Double cpuCores = cpuCores_instances.get(referenceDeployment);
            final Double cpuMemory = memory_instances.get(referenceDeployment);
            final Double cpuMinutes = cpuMinutes_instances.get(referenceDeployment);
            final Double cpuPeakMin = peakCpuMinutes_instances.get(referenceDeployment);
            Set<Double> a = new HashSet<>(Arrays.asList(cpuCores, cpuMinutes, cpuPeakMin, cpuMemory));
            a.remove(null);
            required_instances.put(referenceDeployment, Collections.max(a));
        });
        return required_instances;
    }

    public List<String> errors() {
        List<String> errors = new ArrayList<>();
        errors.addAll(minimumUnit.errors());
        errors.addAll(optimalUnit.errors());
        return errors;
    }
}


