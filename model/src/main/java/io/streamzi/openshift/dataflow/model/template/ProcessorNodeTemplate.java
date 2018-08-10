package io.streamzi.openshift.dataflow.model.template;

import io.streamzi.openshift.dataflow.model.ProcessorInputPort;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import io.streamzi.openshift.dataflow.model.ProcessorOutputPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple template for a node that doesn't hold any links, status etc
 * @author hhiden
 */
public class ProcessorNodeTemplate {
    private String id = "processor";
    private String name = "Unnamed Processor";
    private String description = "A processor node";


    private String transport = "kafka";
    private List<ProcessorNodeTemplatePort> inputs = new ArrayList<>();
    private List<ProcessorNodeTemplatePort> outputs = new ArrayList<>();

    private String mainClassName = "io.streamzi.openshift.container.ProcessorRunner";
    private String imageName = "oc-stream-container";
    private Map<String, String> settings = new HashMap<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public List<ProcessorNodeTemplatePort> getInputs() {
        return inputs;
    }

    public void setInputs(List<ProcessorNodeTemplatePort> inputs) {
        this.inputs = inputs;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public List<ProcessorNodeTemplatePort> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorNodeTemplatePort> outputs) {
        this.outputs = outputs;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
    
    public void addInput(String name, String transportType){
        ProcessorNodeTemplatePort p = new ProcessorNodeTemplatePort();
        p.setName(name);
        p.setTransportType(transportType);
        this.inputs.add(p);
    }
    
    public void addInput(String name) {
        ProcessorNodeTemplatePort p = new ProcessorNodeTemplatePort();
        p.setName(name);
        this.inputs.add(p);
    }

    public void addOutput(String name, String transportType){
        ProcessorNodeTemplatePort p = new ProcessorNodeTemplatePort();
        p.setName(name);
        p.setTransportType(transportType);        
        this.outputs.add(p);
    }
    
    public void addOutput(String name){
        ProcessorNodeTemplatePort p = new ProcessorNodeTemplatePort();
        p.setName(name);        
        this.outputs.add(p);
    }
    
    public ProcessorNode createProcessorNode(){
        ProcessorNode node = new ProcessorNode();
        
        node.setImageName(imageName);
        node.setSettings(settings);
        if(inputs!=null){
            for(ProcessorNodeTemplatePort input : inputs){
                node.addInput(new ProcessorInputPort(input.getName(), input.getTransportType()));
            }
        }
        
        if(outputs!=null){
            for(ProcessorNodeTemplatePort output : outputs){
                node.addOutput(new ProcessorOutputPort(output.getName(), output.getTransportType()));
            }
        }
        return node;
    }

}
