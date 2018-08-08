package io.streamzi.openshift.dataflow.partitioning;

import io.streamzi.openshift.dataflow.model.ProcessorFlow;

import io.streamzi.openshift.dataflow.model.ProcessorLink;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeployment;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentGroup;
import io.streamzi.openshift.dataflow.model.deployment.ProcessorDeploymentMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Breaks up a processor flow into partitions based on runtime location requirements
 * @author hhiden
 */
public class ProcessorFlowPartitioner {
    private ProcessorFlow flow;
    private ProcessorDeploymentMap map;

    public ProcessorFlowPartitioner(ProcessorFlow flow, ProcessorDeploymentMap map) {
        this.flow = flow;
        this.map = map;
    }
    
    public Collection<ProcessorFlow> partition() throws PartitioningException {
        Map<String, ProcessorFlow> results = new HashMap<>();
        
        // Create a set of flows
        List<String> hostIds = map.getHosts();
        ProcessorFlow fragment;
        for(String hostId : hostIds){
            if(map.hostContainsDeployments(hostId)){
                fragment = flow.getCopy();
                fragment.setName(flow.getName() + "-" + hostId);
                results.put(hostId, fragment);
            }
        }
        
        // Add all of the processor nodes
        ProcessorFlow deploymentFlow;
        for(ProcessorNode node : flow.getNodes()){
            ProcessorDeploymentGroup deployments = map.getDeploymentsForProcessor(node.getUuid());
            if(deployments!=null){
                // Add a copy of the node to each partition
                for(ProcessorDeployment d : deployments.getLocations()){
                    deploymentFlow = results.get(d.getHostId());
                    if(deploymentFlow!=null){
                        deploymentFlow.getDeployments().put(node.getUuid(), Integer.toString(d.getReplicas()));
                    } else {
                        throw new PartitioningException("No deployment flow exists for host: " + d.getHostId());
                    }
                }
            } else {
                throw new PartitioningException("No deployments for node: " + node.getTemplateName() + "[" + node.getUuid() + "]");
            }
        }
        
        // Create virtual input and output nodes for any links that have been broken
        return results.values();
    }

}