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

package io.streamzi.eventflow;

import io.fabric8.openshift.client.OpenShiftClient;
import io.streamzi.eventflow.serialization.SerializedFlow;

/**
 * This base class keeps track of a deployment CR and builds the relevant
 * K8s objects to reflect it in a specific environment.
 */
public abstract class TargetStateProvider {
    protected SerializedFlow flow;
    protected OpenShiftClient client;

    public void setFlow(SerializedFlow flow) {
        this.flow = flow;
    }

    public SerializedFlow getFlow() {
        return flow;
    }

    public void setClient(OpenShiftClient client) {
        this.client = client;
    }

    public OpenShiftClient getClient() {
        return client;
    }
    
    public abstract void build() throws Exception;
    public abstract void debugPrint() throws Exception;
}