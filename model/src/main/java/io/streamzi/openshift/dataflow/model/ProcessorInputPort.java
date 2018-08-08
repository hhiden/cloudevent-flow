package io.streamzi.openshift.dataflow.model;

/**
 * An input port
 * @author hhiden
 */
public class ProcessorInputPort extends ProcessorPort {

    public ProcessorInputPort(String name) {
        super(name);
    }

    public ProcessorInputPort(String name, String transportType){
        super(name, transportType);
    }
    
    public ProcessorInputPort() {
        super();
    }

    @Override
    public void addLink(ProcessorLink link) {
        if(links.isEmpty()){
            links.add(link);
        }
    }
    
}