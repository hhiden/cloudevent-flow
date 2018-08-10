package io.streamzi.openshift.dataflow.model.serialization;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.streamzi.openshift.dataflow.model.ProcessorInputPort;
import io.streamzi.openshift.dataflow.model.ProcessorNode;
import io.streamzi.openshift.dataflow.model.ProcessorOutputPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serialized form of node
 * @author hhiden
 */
public class SerializedNode {
    @JsonIgnore
    private ProcessorNode node;

    private String uuid;
    private String templateName;
    private String templateId;

    private String transport;
    private List<SerializedPort> inputs = new ArrayList<>();
    private List<SerializedPort> outputs = new ArrayList<>();

    private String imageName;
    private Map<String, String> settings = new HashMap<>();
    
    public SerializedNode() {
    }

    public SerializedNode(ProcessorNode node) {
        this.node = node;
        uuid = node.getUuid();
        templateId = node.getTemplateId();
        templateName = node.getTemplateName();
        transport = node.getTransport();
                
        this.imageName = node.getImageName();
        for(String key : node.getSettings().keySet()){
            settings.put(key, node.getSettings().get(key));
        }
        
        for(ProcessorOutputPort output : node.getOutputs().values()){
            this.outputs.add(new SerializedPort(output));
        }
        
        for(ProcessorInputPort input : node.getInputs().values()){
            this.inputs.add(new SerializedPort(input));
        }
    }

    public ProcessorNode createNode(){
        ProcessorNode node = new ProcessorNode();
        
        node.setUuid(uuid);
        node.setImageName(imageName);
        node.setSettings(settings);
        node.setTemplateId(templateId);
        node.setTemplateName(templateName);
        node.setTransport(transport);

        for(SerializedPort input : inputs){
            node.addInput(new ProcessorInputPort(input.getName(), input.getTransportType()));
        }
        for(SerializedPort output : outputs){
            node.addOutput(new ProcessorOutputPort(output.getName(), output.getTransportType()));
        }
        
        return node;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public List<SerializedPort> getInputs(){
        return inputs;
    }

    public void setInputs(List<SerializedPort> inputs) {
        this.inputs = inputs;
    }

    public List<SerializedPort> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<SerializedPort> outputs) {
        this.outputs = outputs;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }
}
