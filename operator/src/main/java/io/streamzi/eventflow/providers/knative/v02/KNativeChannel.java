/*
 * Copyright 2019 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.streamzi.eventflow.providers.knative.v02;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * CR For a knative channel
 */
public class KNativeChannel extends CustomResource {
    private LinkedHashMap<String, Object> spec;
    private LinkedHashMap<String, Object> status;
    public KNativeChannel() {

    }

    public LinkedHashMap<String, Object> getStatus() {
        return status;
    }

    public void setStatus(LinkedHashMap<String, Object> status) {
        this.status = status;
    }

    
    public LinkedHashMap<String, Object> getSpec() {
        return spec;
    }

    public void setSpec(LinkedHashMap<String, Object> spec) {
        this.spec = spec;
        for(String key : spec.keySet()){
            System.out.println(key + "[" + spec.get(key).getClass().getSimpleName() + "]: " + spec.get(key));
        }
    }
    
    public void createSpec(String channelName){
        setKind("Channel");
        LinkedHashMap<String, Object> provisioner = new LinkedHashMap<>();
        provisioner.put("apiVersion", "eventing.knative.dev/v1alpha1");
        provisioner.put("kind", "ClusterChannelProvisioner");
        provisioner.put("name", "in-memory-channel");
        spec = new LinkedHashMap<>();
        spec.put("provisioner", provisioner);
        ObjectMeta md = new ObjectMeta();
        md.setName(channelName);
        setMetadata(md);
    }
}