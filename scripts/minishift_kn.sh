#!/usr/bin/env bash

set -e

# Turn colors in this script off by setting the NO_COLOR variable in your
# environment to any value:
#
# $ NO_COLOR=1 test.sh
NO_COLOR=${NO_COLOR:-""}
if [ -z "$NO_COLOR" ]; then
  header=$'\e[1;33m'
  reset=$'\e[0m'
else
  header=''
  reset=''
fi


function header_text {
  echo "$header$*$reset"
}


# make sure the profile is set correctly
minishift profile set knative
# Pinning to the right unreleased version needed in this case v3.11.0
minishift config set openshift-version v3.11.0
# memory for the vm
minishift config set memory 10GB
# the vCpus for the vm
minishift config set cpus 4
# extra disk size for the vm
minishift config set disk-size 50g
# caching the images that will be downloaded during app deployments
minishift config set image-caching true
# Add new user called admin with password with role cluster-admin
minishift addons enable admin-user
# Allow the containers to be run with any uid
minishift addons enable anyuid
# Start minishift
minishift start --vm-driver virtualbox






# Enable admission controller webhooks
# The configuration stanzas below look weird and are just to workaround
# https://bugzilla.redhat.com/show_bug.cgi?id=1635918
minishift openshift config set --target=kube --patch '{
    "admissionConfig": {
        "pluginConfig": {
            "ValidatingAdmissionWebhook": {
                "configuration": {
                    "apiVersion": "apiserver.config.k8s.io/v1alpha1",
                    "kind": "WebhookAdmission",
                    "kubeConfigFile": "/dev/null"
                }
            },
            "MutatingAdmissionWebhook": {
                "configuration": {
                    "apiVersion": "apiserver.config.k8s.io/v1alpha1",
                    "kind": "WebhookAdmission",
                    "kubeConfigFile": "/dev/null"
                }
            }
        }
    }
}'
 
eval $(minishift oc-env)
until oc login -u admin -p admin; do sleep 5; done;
 
# Setup the project myproject for Knative use
oc project myproject
oc adm policy add-scc-to-user privileged -z default
oc adm policy add-scc-to-user anyuid -z default


header_text "Setting up istio"
oc adm policy add-scc-to-user privileged -z default -n myproject
oc label namespace default istio-injection=myproject



oc adm policy add-scc-to-user anyuid -z istio-ingress-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z default -n istio-system
oc adm policy add-scc-to-user anyuid -z prometheus -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-egressgateway-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-citadel-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-ingressgateway-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-cleanup-old-ca-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-mixer-post-install-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-mixer-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-pilot-service-account -n istio-system
oc adm policy add-scc-to-user anyuid -z istio-sidecar-injector-service-account -n istio-system
oc adm policy add-cluster-role-to-user cluster-admin -z istio-galley-service-account -n istio-system




oc apply --filename "https://github.com/knative/serving/releases/download/v0.5.1/istio-crds.yaml" &&
    curl -L "https://github.com/knative/serving/releases/download/v0.5.1/istio.yaml" \
        | sed 's/LoadBalancer/NodePort/' \
        | oc apply --filename -

sleep 5; while echo && oc get pods -n istio-system | grep -v -E "(Running|Completed|STATUS)"; do sleep 5; done


header_text "Setting up security policy for knative"
oc adm policy add-scc-to-user anyuid -z controller -n knative-serving
oc adm policy add-scc-to-user anyuid -z autoscaler -n knative-serving


curl -L "https://github.com/knative/serving/releases/download/v0.5.1/serving.yaml" \
  | sed 's/LoadBalancer/NodePort/' \
  | oc apply --filename -

oc adm policy add-cluster-role-to-user cluster-admin -z controller -n knative-serving


header_text "Waiting for Knative to become ready"
sleep 5; while echo && oc get pods -n knative-serving | grep -v -E "(Running|Completed|STATUS)"; do sleep 5; done


header_text "Setting up Knative Eventing"

oc adm policy add-scc-to-user anyuid -z eventing-controller -n knative-eventing
oc adm policy add-scc-to-user anyuid -z eventing-webhook -n knative-eventing
oc adm policy add-scc-to-user privileged -z eventing-webhook -n knative-eventing
oc adm policy add-scc-to-user anyuid -z in-memory-channel-dispatcher -n knative-eventing
oc adm policy add-scc-to-user anyuid -z in-memory-channel-controller -n knative-eventing

## Eventing and Eventing-Sources
oc apply -f https://github.com/knative/eventing/releases/download/v0.5.0/release.yaml
oc apply -f https://github.com/knative/eventing-sources/releases/download/v0.5.0/eventing-sources.yaml

oc adm policy add-cluster-role-to-user cluster-admin -z eventing-controller -n knative-eventing
oc adm policy add-cluster-role-to-user cluster-admin -z eventing-webhook -n knative-eventing
oc adm policy add-cluster-role-to-user cluster-admin -z in-memory-channel-dispatcher -n knative-eventing
oc adm policy add-cluster-role-to-user cluster-admin -z in-memory-channel-controller -n knative-eventing
oc adm policy add-cluster-role-to-user cluster-admin -z default -n knative-sources

sleep 5; while echo && kubectl get pods -n knative-eventing | grep -v -E "(Running|Completed|STATUS)"; do sleep 5; done
sleep 5; while echo && kubectl get pods -n knative-sources | grep -v -E "(Running|Completed|STATUS)"; do sleep 5; done

header_text "Waiting for Knative Eventing to become ready"

