# NGSI-LD Broker Manager

NGSI-LD Broker Manager is a component developed in Java with Spring.
Its purpose is to communicate with [Idra](https://github.com/OPSILab/Idra) and the [Orion Context Broker-LD](https://github.com/FIWARE/context.Orion-LD) 
in order to map the DCAT-AP metadata of the federated catalogues in Idra, according to the NGSI-LD meta-model.
To do this, these [specifications](https://github.com/smart-data-models/dataModel.DCAT-AP) 
are used.

By using this external and optional component, the DCAT-AP metadata are provided in NGSI-LD in order to be accessible 
also through the Context Broker and take advantage from its publish and subscribe approach.
This approach that allows context data subscriptions is described in its [documentation](https://ngsi-ld-tutorials.readthedocs.io/en/latest/subscriptions.html).

## Asynchronous notifications

The Orion Context Broker offers also an asynchronous notification mechanism: applications can subscribe to changes of context 
information so that they can be informed when something happens. This means the application does not need to continuously poll 
or repeat query requests.

It is possible to request the sending of notifications to a specific URL, to be specified within the subscription.
To view all the subscriptions currently active in your Context Broker, you must send the following request:
```yml
curl localhost:1026/ngsi-ld/v1/subscriptions/
```

Below are some examples of requests to the Context Broker to obtain information about the federated catalogues in Idra.

### Request for notification regarding the federation of a specific catalogue

```yml
{
  "description": "Notify me of all Catalogues changes",
  "type": "Subscription",
  "entities": [{"type": "CatalogueDCAT-AP"}],
  "watchedAttributes": ["title"],
  "q": "title==Civitavecchia Open Data",
  "notification": {
    "format": "keyValues",
    "endpoint": {
      "uri": "https://webhook.site/532c1717-a61a-4352-8457-6eedf55a6597",
      "accept": "application/json"
    }
  }
}
```

### Request for notification relating to the modification of the metadata of a Dataset or to the addition of a new Dataset

```yml
{
  "description": "Notify me on the creation or modification of a Dataset",
  "type": "Subscription",
  "entities": [{"type": "Dataset"}],
  "notification": {
    "attributes": ["title"],
    "format": "normalized",
    "endpoint": {
      "uri": "http://host.docker.internal:8080/Idra/api/v1/client/notification/push",
      "accept": "application/json"
    }
  }
}
```