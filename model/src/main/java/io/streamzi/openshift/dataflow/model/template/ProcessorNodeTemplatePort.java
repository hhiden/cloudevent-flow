/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.streamzi.openshift.dataflow.model.template;

/**
 *
 * @author hhiden
 */
public class ProcessorNodeTemplatePort {

    private String name;
    private String transportType = "defaut";

    public ProcessorNodeTemplatePort() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
}
