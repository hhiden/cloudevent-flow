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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.streamzi.eventflow.TargetStateProvider;
import io.streamzi.eventflow.serialization.SerializedLink;
import io.streamzi.eventflow.serialization.SerializedNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Deployer / state manager for KNative 0.2
 */
public class KNativeTargetState extends TargetStateProvider {

    private static final Logger logger = Logger.getLogger(KNativeTargetState.class.getName());

    @Override
    public void build() throws Exception {
        logger.info("Flow build");
        ObjectMapper mapper = new ObjectMapper();
        logger.info(mapper.writeValueAsString(getFlow()));

        // List channels that need to be provisioned
        Map<String, KNativeChannel> channels = createNewChannelList();
        for (KNativeChannel ch : channels.values()) {
            logger.info("Creating channel: " + ch.getMetadata().getName());
            createChannel(ch);
        }

        // List subscriptions that need to be created
        List<KNativeSubscription> subscriptions = createSubscriptionList();
        for (KNativeSubscription s : subscriptions) {
            logger.info("Will create subscription: " + s.toString());
            createSubscription(s);
        }
        
        // List subscriptions that need to be removed
        List<String> subscriptionsToDelete = createSubscriptionRemovalList();
        for(String s : subscriptionsToDelete){
            logger.info("Will delete subscription: " + s);
            deleteSubscription(s);
            logger.info("Deleted: " + s);
        }
    }

    /**
     * Create a list of service->service connections. These will need a channel + associated subscriptions creating
     */
    private Map<String, KNativeChannel> createNewChannelList() {
        HashMap<String, KNativeChannel> results = new HashMap<>();

        SerializedNode source;
        SerializedNode target;
        String channelName;

        for (SerializedLink link : flow.getLinks()) {
            source = flow.findNode(link.getSourceUuid());
            target = flow.findNode(link.getTargetUuid());
            if (source != null && target != null) {
                if (isNodeKService(source) && isNodeKService(target)) {
                    channelName = flow.getName() + "-" + source.getUuid() + "-" + link.getSourcePortName();
                    if (!results.containsKey(channelName)) {
                        KNativeChannel newChannel = new KNativeChannel();
                        newChannel.createSpec(channelName);
                        logger.info("Will provision channel: " + channelName);
                        results.put(channelName, newChannel);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Create a list of subscriptions to remove
     */
    private List<String> createSubscriptionRemovalList(){
        List<String> results = new ArrayList<>();
        for(SerializedNode n : flow.getNodes()){
            if(flow.isNodeIsolated(n.getUuid())){
                results.add(n.getDisplayName() + "-subscription");
            }
        }
        return results;
    }
    
    /**
     * Create a list of event-source->service connections. These will need a subscription creating
     */
    private List<KNativeSubscription> createSubscriptionList() {
        List<KNativeSubscription> results = new ArrayList<>();
        SerializedNode source;
        SerializedNode target;
        String connectedOutput;

        for (SerializedLink link : flow.getLinks()) {
            source = flow.findNode(link.getSourceUuid());
            target = flow.findNode(link.getTargetUuid());
            if (source != null && target != null) {
                // Have we connected a service to an output channel
                if (isNodeKEventSource(source) && isNodeKService(target)) {
                    // Service sends to a channel
                    KNativeSubscription sub = new KNativeSubscription();
                    sub.createServiceSubscriptionSpec(target.getDisplayName() + "-subscription", source.getOutputs().get(0), target.getDisplayName());

                    // Is anything attached to the output of the service - if it is, it needs a reply field set in the subscription
                    connectedOutput = flow.findSingleConnectedOutputFromNode(target.getUuid());
                    if (connectedOutput != null) {

                        // Need to add a reply subscription
                        String replyChannelName = flow.getName() + "-" + target.getUuid() + "-" + connectedOutput;
                        sub.addReplySubscription(replyChannelName);
                    }
                    results.add(sub);

                } else if (isNodeKService(source) && isNodeKService(target)) {
                    // Two services communicating via a reply channel - set up the input subscription and (potentially, a reply
                    String channelName = flow.getName() + "-" + source.getUuid() + "-" + link.getSourcePortName();
                    KNativeSubscription sub = new KNativeSubscription();
                    sub.createServiceSubscriptionSpec(target.getDisplayName() + "-subscription", channelName, target.getDisplayName());

                    // Reply?
                    connectedOutput = flow.findSingleConnectedOutputFromNode(target.getUuid());
                    if (connectedOutput != null) {
                        String replyChannelName = flow.getName() + "-" + target.getUuid() + "-" + connectedOutput;
                        sub.addReplySubscription(replyChannelName);
                    }
                    results.add(sub);

                } else {
                    // Ignore everything else

                }
            }
        }
        return results;
    }

    private boolean isNodeKEventSource(SerializedNode node) {
        if (node.getAttributes().containsKey("type") && node.getAttributes().get("type").equalsIgnoreCase("KContainerSource")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNodeKInputChannel(SerializedNode node) {
        if (node.getAttributes().containsKey("type") && node.getAttributes().get("type").equalsIgnoreCase("KInputChannel")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNodeKOutputChannel(SerializedNode node) {
        if (node.getAttributes().containsKey("type") && node.getAttributes().get("type").equalsIgnoreCase("KOutputChannel")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNodeKService(SerializedNode node) {
        if (node.getAttributes().containsKey("type") && node.getAttributes().get("type").equalsIgnoreCase("kservice")) {
            return true;
        } else {
            return false;
        }
    }

    private void createChannel(KNativeChannel channel) {
        final CustomResourceDefinition channelCRD = client.customResourceDefinitions().withName("channels.eventing.knative.dev").get();
        if (channelCRD != null) {
            KNativeChannel existing = client.customResource(channelCRD, KNativeChannel.class, KNativeChannelList.class, DoneableKNativeChannelList.class).inNamespace(client.getNamespace()).withName(channel.getMetadata().getName()).get();

            if (existing == null) {
                logger.info("Channel doesn't exist: " + channel.getMetadata().getName() + ": creating");
                client.customResources(channelCRD, KNativeChannel.class, KNativeChannelList.class, DoneableKNativeChannelList.class).inNamespace(client.getNamespace()).createOrReplace(channel);
            } else {
                logger.info("Channel: " + channel.getMetadata().getName() + " already exists");
            }

        } else {
            logger.log(Level.SEVERE, "No channel CRD available");
        }
    }

    private void deleteSubscription(String name){
        final CustomResourceDefinition subscriptionCRD = client.customResourceDefinitions().withName("subscriptions.eventing.knative.dev").get();
        if (subscriptionCRD != null) {

            KNativeSubscription existing = client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).withName(name).get();
            if(existing!=null){
                client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).delete(existing);
                while((existing = client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).withName(name).get())!=null){
                    logger.info("still here...");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                    }                        
                }                
            }
        }
    }
    
    private void createSubscription(KNativeSubscription subscription) {
        final CustomResourceDefinition subscriptionCRD = client.customResourceDefinitions().withName("subscriptions.eventing.knative.dev").get();
        if (subscriptionCRD != null) {
            KNativeSubscription existing = client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).withName(subscription.getMetadata().getName()).get();
            if (existing != null) {
                if (!existing.matches(subscription)) {
                    logger.info("Need to replace subscription");

                    client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).delete(existing);
                    while((existing = client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).withName(subscription.getMetadata().getName()).get())!=null){
                        logger.info("still here...");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception ex) {
                        }                        
                    }
                    
                    logger.info("Deleted existing subscription - creating a new one");
                    client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).create(subscription);

                }
            } else {
                client.customResources(subscriptionCRD, KNativeSubscription.class, KNativeSubscriptionList.class, DoneableKNativeSubscriptionList.class).inNamespace(client.getNamespace()).createOrReplace(subscription);
            }

        } else {
            logger.log(Level.SEVERE, "No sunscription CRD available");
        }
    }

    /**
     * Display debugging information
     */
    @Override
    public void debugPrint() throws Exception {
        // Get the channels
        final CustomResourceDefinition channelCRD = client.customResourceDefinitions().withName("channels.eventing.knative.dev").get();
        if (channelCRD == null) {
            logger.severe("Can't find Flow CRD");
        } else {
            logger.info("Channel: " + channelCRD.getMetadata().getName());
        }

        List<KNativeChannel> channels = client.customResources(
                channelCRD,
                KNativeChannel.class,
                KNativeChannelList.class,
                DoneableKNativeChannelList.class)
                .inNamespace(client.getNamespace()).list().getItems().stream()
                .collect(Collectors.toList());

        for (KNativeChannel c : channels) {
            logger.info("Channel: " + c.getMetadata().getName());
        }

        // Get the subscriptions
        final CustomResourceDefinition subscriptionCRD = client.customResourceDefinitions().withName("subscriptions.eventing.knative.dev").get();
        if (subscriptionCRD == null) {
            logger.severe("Can't find Subscription CRD");
        } else {
            logger.info("Subscription: " + subscriptionCRD.getMetadata().getName());
        }

        List<KNativeSubscription> subscriptions = client.customResource(
                subscriptionCRD,
                KNativeSubscription.class,
                KNativeSubscriptionList.class,
                DoneableKNativeSubscriptionList.class)
                .inNamespace(client.getNamespace()).list().getItems().stream()
                .collect(Collectors.toList());

        for (KNativeSubscription s : subscriptions) {
            logger.info("Subscription: " + s.getMetadata().getName());
        }

        // Get the sources
        final CustomResourceDefinition sourceCRD = client.customResourceDefinitions().withName("containersources.sources.eventing.knative.dev").get();
        if (sourceCRD == null) {
            logger.severe("Can't find Source CRD");
        } else {
            logger.info("Source: " + sourceCRD.getMetadata().getName());
        }
    }

}
