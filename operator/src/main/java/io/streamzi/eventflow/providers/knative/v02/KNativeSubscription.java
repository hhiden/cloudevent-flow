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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import java.util.LinkedHashMap;

/**
 *
 */
public class KNativeSubscription extends CustomResource {

    private LinkedHashMap<String, Object> spec;
    private LinkedHashMap<String, Object> status;

    public LinkedHashMap<String, Object> getSpec() {
        return spec;
    }

    public void setSpec(LinkedHashMap<String, Object> spec) {
        this.spec = spec;
    }

    public LinkedHashMap<String, Object> getStatus() {
        return status;
    }

    public void setStatus(LinkedHashMap<String, Object> status) {
        this.status = status;
    }

    public void createServiceSubscriptionSpec(String subscriptionName, String channelName, String subscriberService) {
        setKind("Subscription");
        setApiVersion("eventing.knative.dev/v1alpha1");

        spec = new LinkedHashMap<>();

        LinkedHashMap<String, Object> channel = new LinkedHashMap<>();
        channel.put("apiVersion", "eventing.knative.dev/v1alpha1");
        channel.put("kind", "Channel");
        channel.put("name", channelName);
        spec.put("channel", channel);

        LinkedHashMap<String, Object> subscriber = new LinkedHashMap<>();
        LinkedHashMap<String, Object> ref = new LinkedHashMap<>();
        ref.put("apiVersion", "serving.knative.dev/v1alpha1");
        ref.put("kind", "Service");
        ref.put("name", subscriberService);

        subscriber.put("ref", ref);

        spec.put("subscriber", subscriber);

        ObjectMeta md = new ObjectMeta();
        md.setName(subscriptionName);
        setMetadata(md);
    }

    public void addReplySubscription(String channelName) {
        LinkedHashMap<String, Object> reply = new LinkedHashMap<>();
        LinkedHashMap<String, Object> channel = new LinkedHashMap<>();
        channel.put("apiVersion", "eventing.knative.dev/v1alpha1");
        channel.put("kind", "Channel");
        channel.put("name", channelName);
        reply.put("channel", channel);
        spec.put("reply", reply);
    }

    public boolean matches(KNativeSubscription sub) {

        String thisChannel = getChannelName();
        String subChannel = sub.getChannelName();
        if (thisChannel.equals(subChannel)) {
            if (getReplyChannelName() == null && sub.getReplyChannelName() == null) {
                // No replies
                return true;
            } else {
                String thisReply = getReplyChannelName();
                String subReply = sub.getReplyChannelName();
                if(thisReply!=null && subReply!=null){
                    if(thisReply.equals(subReply)){
                        return true;
                    } else {
                        return false;
                    }
                } else if(thisReply!=null && subReply==null){
                    return false;
                } else if(thisReply==null &&subReply!=null){
                    return false;
                } else {
                    return false;
                }
            }

        } else {
            return false;
        }
        

    }

    @JsonIgnore
    public String getReplyChannelName() {
        if (spec.containsKey("reply")) {
            LinkedHashMap<String, Object> reply = (LinkedHashMap<String, Object>) spec.get("reply");
            LinkedHashMap<String, Object> replyChannel = (LinkedHashMap<String, Object>) reply.get("channel");
            return replyChannel.get("name").toString();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public String getChannelName() {
        if (spec.containsKey("channel")) {
            return ((LinkedHashMap<String, Object>) getSpec().get("channel")).get("name").toString();
        } else {
            return null;
        }
    }
}
