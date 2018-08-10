package io.streamzi.openshift.dataflow.model.serialization;

import io.streamzi.openshift.dataflow.model.ProcessorPort;

/**
 * Serialization data for a processor port
 * @author hhiden
 */
public class SerializedPort {
    private String name;
    private String transportType;

    public SerializedPort(ProcessorPort port){
        this.name = port.getName();
        this.transportType = port.getTransportType();
    }
    
    public SerializedPort() {
    }

    public String getName() {
        return name;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}