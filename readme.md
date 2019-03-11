## Knative eventing demo - WIP

### Keywords
- knative
  - build
  - eventing
  - eventing-source
- serverless
- spring cloud function
- [Jib Build](https://github.com/knative/build-templates/tree/master/jib)

### Setup

#### Build
```sh
kubectl apply -f https://raw.githubusercontent.com/knative/build-templates/master/jib/jib-gradle.yaml

VER=v1
gradle jib --image=skylab00/gcp-filesource:${VER} \
  -Djib.to.auth.username=$DOCKER_HUB_ID \
  -Djib.to.auth.password=$DOCKER_HUB_PASS
```

#### Create Source
```sh
kubectl apply -f cfg/gcp-filesource.yaml
```
