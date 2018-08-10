package io.streamzi.openshift.dataflow.model;

import io.streamzi.openshift.dataflow.model.serialization.SerializedNode;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a node that processes CloudEvents. It contains
 * details of input and output connections and the code to instantiate
 * when being deployed
 * @author hhiden
 */
public class ProcessorNode extends ProcessorObject {
    /** Input Ports */
    protected Map<String, ProcessorInputPort> inputs = new HashMap<>();
    
    /** Output Ports */
    protected Map<String, ProcessorOutputPort> outputs = new HashMap<>();
    
    /** Image name */
    protected String imageName = "oc-stream-container";
    
    /** Unique ID of the node */
    protected String uuid;

    /** Name of the node taken from the template */
    protected String templateName;
    
    /** ID of the node template */
    protected String templateId;
    
    /** Runtime settings */
    protected Map<String, String> settings = new HashMap<>();
    
    /** Parent flow */
    protected ProcessorFlow parent;
    
    public ProcessorNode() {
    }

    public ProcessorNode getCopy(){
        ProcessorNode copy = new ProcessorNode();
        copy.setImageName(imageName);
        copy.setTemplateId(templateId);
        copy.setTemplateName(templateName);
        copy.setUuid(uuid);
        
        
        for(String key : settings.keySet()){
            copy.settings.put(key, settings.get(key));
        }
        
        for(ProcessorOutputPort port : outputs.values()){
            copy.addOutput(new ProcessorOutputPort(port.getName(), port.getTransportType()));
        }
        
        for(ProcessorInputPort port : inputs.values()){
            copy.addInput(new ProcessorInputPort(port.getName(), port.getTransportType()));
        }

        return copy;
    }
    
    public ProcessorFlow getParent() {
        return parent;
    }

    public void setParent(ProcessorFlow parent) {
        this.parent = parent;
    }
       
    public Map<String, ProcessorInputPort> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, ProcessorInputPort> inputs) {
        this.inputs = inputs;
    }

    public Map<String, ProcessorOutputPort> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, ProcessorOutputPort> outputs) {
        this.outputs = outputs;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public void addInput(ProcessorInputPort input){
        input.setParent(this);
        inputs.put(input.getName(), input);
    }
    
    public void addOutput(ProcessorOutputPort output){
        output.setParent(this);
        outputs.put(output.getName(), output);
    }
    
    public ProcessorOutputPort getOutput(String name){
        return outputs.get(name);
    }
    
    public ProcessorInputPort getInput(String name){
        return inputs.get(name);
    } 

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}