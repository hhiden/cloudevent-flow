/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.streamzi.openshift.dataflow.model.deployment;

/**
 *
 * @author hhiden
 */
public class ProcessorDeploymentException extends Exception {

    /**
     * Creates a new instance of <code>ProcessorDeploymentException</code> without detail message.
     */
    public ProcessorDeploymentException() {
    }

    /**
     * Constructs an instance of <code>ProcessorDeploymentException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ProcessorDeploymentException(String msg) {
        super(msg);
    }
}
