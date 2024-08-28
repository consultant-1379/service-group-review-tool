package snt.rmrt.models.rmrt.repository.resourceModel.scaleUnit;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class Unit implements Serializable {

    private String cpuCores = "0";
    private String parsedCpuCores;
    private String memory = "0";
    private String parsedMemory;
    private String cpuMinutes = "0";
    private String parsedCpuMinutes;
    private String peakCpuMinutes = "0";
    private String parsedPeakCpuMinutes;

    private String cpuCores_error;
    private String cpuMinutes_error;
    private String peakCpuMinutes_error;
    private String memory_error;

    private Map<String, Double> cpuCores_values = new HashMap<>();
    private Map<String, Double> memory_values = new HashMap<>();
    private Map<String, Double> cpuMinutes_values = new HashMap<>();
    private Map<String, Double> peakCpuMinutes_values = new HashMap<>();

    public List<String> errors() {
        Set<String> errors = new HashSet<>();
        errors.add(cpuCores_error);
        errors.add(memory_error);
        errors.add(cpuMinutes_error);
        errors.add(peakCpuMinutes_error);
        errors.remove(null);
        return new ArrayList<>(errors);
    }

    public String getParsedCpuCores() {
        if(parsedCpuCores == null) {
            parsedCpuCores = cpuCores;
        }
        return parsedCpuCores;
    }

    public String getParsedMemory() {
        if(parsedMemory == null) {
            parsedMemory = memory;
        }
        return parsedMemory;
    }

    public String getParsedCpuMinutes() {
        if(parsedCpuMinutes == null) {
            parsedCpuMinutes = cpuMinutes;
        }
        return parsedCpuMinutes;
    }

    public String getParsedPeakCpuMinutes() {
        if(parsedPeakCpuMinutes == null) {
            parsedPeakCpuMinutes = peakCpuMinutes;
        }
        return parsedPeakCpuMinutes;
    }
}