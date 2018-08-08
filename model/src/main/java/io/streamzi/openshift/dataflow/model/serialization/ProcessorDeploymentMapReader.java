package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMap;
import java.io.IOException;

/**
 * Reads a deployment map from JSON
 * @author hhiden
 */
public class ProcessorDeploymentMapReader {
    public ProcessorDeploymentMap read(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ProcessorDeploymentMap.class);
    }
}
