package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMap;

/**
 * Writes a deployment group to JSON
 * @author hhiden
 */
public class ProcessorDeploymentGroupWriter {
    private ProcessorDeploymentMap map;

    public ProcessorDeploymentGroupWriter(ProcessorDeploymentMap map) {
        this.map = map;
    }
    
    public String writeToJsonString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }
    
    public String writeToIndentedJsonString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT,  true);
        return mapper.writeValueAsString(map);
    }
}
