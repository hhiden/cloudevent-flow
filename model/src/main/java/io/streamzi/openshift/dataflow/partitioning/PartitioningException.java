/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.streamzi.openshift.dataflow.partitioning;

/**
 *
 * @author hhiden
 */
public class PartitioningException extends Exception {

    /**
     * Creates a new instance of <code>PartitioningException</code> without detail message.
     */
    public PartitioningException() {
    }

    /**
     * Constructs an instance of <code>PartitioningException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public PartitioningException(String msg) {
        super(msg);
    }
}
