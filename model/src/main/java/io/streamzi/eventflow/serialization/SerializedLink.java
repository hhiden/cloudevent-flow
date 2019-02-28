package io.streamzi.eventflow.serialization;

/**
 * @author hhiden
 */
public class SerializedLink {
    private String sourceUuid;
    private String targetUuid;
    private String sourcePortName;
    private String targetPortName;

    public SerializedLink() {
    }


    public String getSourcePortName() {
        return sourcePortName;
    }

    public void setSourcePortName(String sourcePortName) {
        this.sourcePortName = sourcePortName;
    }

    public String getTargetPortName() {
        return targetPortName;
    }

    public void setTargetPortName(String targetPortName) {
        this.targetPortName = targetPortName;
    }

    public String getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    @Override
    public String toString() {
        return "SerializedLink{" +
                ", sourceUuid='" + sourceUuid + '\'' +
                ", targetUuid='" + targetUuid + '\'' +
                ", sourcePortName='" + sourcePortName + '\'' +
                ", targetPortName='" + targetPortName + '\'' +
                '}';
    }
}
