package io.streamzi.openshift.dataflow.model.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds a group of deployment preferences for a ProcessorNode.
 * @author hhiden
 */
public class ProcessorDeploymentGroup {
    private String processorUuid;
    private Map<String, ProcessorDeployment> deployments = new HashMap<>();
    
    public ProcessorDeploymentGroup(String processorUuid) {
        this.processorUuid = processorUuid;
    }
    
    public void addDeployment(String hostId, int replicas){
        if(deployments.containsKey(hostId)){
            // Create a new deployment
            deployments.put(hostId, new ProcessorDeployment(hostId, replicas));
        } else {
            // Add to an existing deployment
            if(deployments.get(hostId).incrementReplicas(replicas)==0){
                // None left, remove
                deployments.remove(hostId);
            }
        }
    }
}
