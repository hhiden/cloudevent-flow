package io.streamzi.openshift.dataflow.model.deployment;

import io.streamzi.openshift.dataflow.model.ProcessorFlow;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Builder class for creating deployment maps for a processor flow
 * @author hhiden
 */
public class ProcessorDeploymentMapBuilder {
    private ProcessorFlow flow;
    private List<String> hostIds = new ArrayList<>();
    private Map<String, ProcessorDeploymentGroup> deploymentSet = new HashMap<>();
    
    private String defaultHostId;
    private int defaultReplicas = 1;
    
    public ProcessorDeploymentMapBuilder(ProcessorFlow flow) {
        this.flow = flow;
        for(ProcessorNode node : flow.getNodes()){
            deploymentSet.put(node.getUuid(), new ProcessorDeploymentGroup(node.getUuid()));
        }
    }
    
    public ProcessorDeploymentMapBuilder(ProcessorFlow flow, ProcessorDeploymentMap existingMap){
        this.flow = flow;
        
        // Add existing deployments
        Iterator<ProcessorDeploymentGroup> existingGroups = existingMap.groupsIterator();
        ProcessorDeploymentGroup group;
        while(existingGroups.hasNext()){
            group = existingGroups.next();
            if(flow.containesNode(group.getProcessorUuid())){
                // Corresponding node
                deploymentSet.put(group.getProcessorUuid(), group);
            }
        }
        
        // Any thing missing
        for(ProcessorNode node : flow.getNodes()){
            if(!deploymentSet.containsKey(node.getUuid())){
                deploymentSet.put(node.getUuid(), new ProcessorDeploymentGroup(node.getUuid()));
            }
        }
    }
    
    public ProcessorDeploymentMapBuilder setDefaultReplicase(int defaultReplicas){
        this.defaultReplicas = defaultReplicas;
        return this;
    }
    
    public ProcessorDeploymentMapBuilder setDefaultDeploymentHost(String hostId){
        if(!hostIds.contains(hostId)){
            hostIds.add(hostId);
        }
        defaultHostId = hostId;
        return this;
    }
    
    public ProcessorDeploymentMapBuilder addDeploymentHost(String hostId){
        hostIds.add(hostId);
        return this;
    }
    
    public ProcessorDeploymentMapBuilder addDeployment(ProcessorNode node, String hostId, int replicas){
        return addDeployment(node.getUuid(), hostId, replicas);
    }
    
    public ProcessorDeploymentMapBuilder addDeployment(ProcessorNode node, String hostId){
        return addDeployment(node.getUuid(), hostId, defaultReplicas);
    }
    
    public ProcessorDeploymentMapBuilder addDeployment(String processorUuid, String hostId, int replicas){
        ProcessorDeploymentGroup g = deploymentSet.get(processorUuid);
        if(g==null){
            g = new ProcessorDeploymentGroup(processorUuid);
            deploymentSet.put(processorUuid, g);
        }
        g.addDeployment(hostId, replicas);
        return this;
    }
    
    public ProcessorDeploymentMapBuilder deployAllToDefaultHost(){
        for(ProcessorDeploymentGroup g : deploymentSet.values()){
            g.clearDeployments();
            g.addDeployment(defaultHostId, defaultReplicas);
        }
        return this;
    }
    
    public ProcessorDeploymentMapBuilder assignDefaultDeploymentForUndeployedProcessors() throws ProcessorDeploymentException {
        if(defaultHostId!=null){
            for(ProcessorDeploymentGroup g : deploymentSet.values()){
                if(!g.isConfigured()){
                    g.addDeployment(defaultHostId, defaultReplicas);
                }
            }
            return this;
        } else {
            throw new ProcessorDeploymentException("No default host defined");
        }
    }
    
    public ProcessorDeploymentMap build() throws ProcessorDeploymentException {
        if(allDeploymentsConfigured()){
            ProcessorDeploymentMap map = new ProcessorDeploymentMap();
            
            for(ProcessorDeploymentGroup g : deploymentSet.values()){
                map.addDeploymentsForProcessor(g.getProcessorUuid(), g);
            }
            map.setHosts(hostIds);
            return map;
        } else {
            return new ProcessorDeploymentMap();
            //throw new ProcessorDeploymentException("Not all processors have been assigned to a host");
        }
    }
    
    public boolean allDeploymentsConfigured(){
        for(ProcessorDeploymentGroup g : deploymentSet.values()){
            if(!g.isConfigured()){
                return false;
            }
        }
        return true;
    }
}
