package io.streamzi.eventflow.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serialized form of node
 *
 * @author hhiden
 */
public class SerializedNode {
    private String uuid = UUID.randomUUID().toString();
    private String displayName;
    private List<String> inputs = new ArrayList<>();
    private List<String> outputs = new ArrayList<>();
    private HashMap<String, String> attributes = new HashMap<>();
    private Map<String, String> settings = new HashMap<>();
    private String templateName;
    private String imageName;
    private String processorType;
    
    public SerializedNode() {
    }

    public String getUuid() {
        return uuid;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getProcessorType() {
        return processorType;
    }

    public void setProcessorType(String processorType) {
        this.processorType = processorType;
    }

    @Override
    public String toString() {
        return "SerializedNode{" +
                ", uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", imageName='" + imageName + '\'' +
                ", settings=" + settings +
                '}';
    }
}
