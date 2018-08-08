package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.streamzi.openshift.dataflow.model.deployment.DeploymentHost;
import java.util.List;

/**
 * Stores a list of deployment hosts to JSON
 * @author hhiden
 */
public class DeploymentHostListWriter {
    private List<DeploymentHost> hosts;

    public DeploymentHostListWriter(List<DeploymentHost> hosts) {
        this.hosts = hosts;
    }
    
    public String writeToIndentedJsonString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT,  true);
        return mapper.writeValueAsString(hosts);        
    }
}
