package io.streamzi.openshift.dataflow.partitioning;

import io.streamzi.openshift.dataflow.model.ProcessorFlow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Breaks up a processor flow into partitions based on runtime location requirements
 * @author hhiden
 */
public class ProcessorFlowPartitioner {
    private ProcessorFlow flow;


    public ProcessorFlowPartitioner(ProcessorFlow flow) {
        this.flow = flow;
    }
    
    public List<ProcessorFlow> partition(){
        List<ProcessorFlow> results = new ArrayList<>();
        
        return results;
    }
}