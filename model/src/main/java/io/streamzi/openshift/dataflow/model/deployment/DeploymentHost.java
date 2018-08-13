package io.streamzi.openshift.dataflow.model.deployment;

/**
 * Representation of a host that can have dataflows posted to it. It is a holder
 * for information such as the API endpoint, cloud type and any credentials requried
 * to access it.
 * @author hhiden
 */
public class DeploymentHost {
    private String id;
    private String url;
    private String secretId;    // Link to a secret in the container platform

    public DeploymentHost() {
    }

    public DeploymentHost(String id) {
        this.id = id;
    }

    public DeploymentHost(String id, String url, String secretId) {
        this.id = id;
        this.url = url;
        this.secretId = secretId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }
}