# NGSI-LD Broker Manager

NGSI-LD Broker Manager is a component developed in Java with Spring.
Its purpose is to communicate with [Idra](https://github.com/OPSILab/Idra) and with a Context Broker, such as
the [Orion-LD Context Broker](https://github.com/FIWARE/context.Orion-LD) in order to map the DCAT-AP metadata 
of the federated catalogues in Idra, according to the NGSI-LD meta-model.
To do this, these [specifications](https://github.com/smart-data-models/dataModel.DCAT-AP) are used.

By using this external and optional component, the DCAT-AP metadata are provided in NGSI-LD in order to be accessible 
also through the Context Broker and take advantage from its publish and subscribe approach.
This approach that allows context data subscriptions is described in its [documentation](https://ngsi-ld-tutorials.readthedocs.io/en/latest/subscriptions.html).

The possible operations, through REST APIs, to communicate with the Context Broker are defined in the specific
[documentation](https://ngsi-ld-tutorials.readthedocs.io/en/latest/ngsi-ld-operations.html).

The NGSI-LD Broker Manager is a software developed inside the EU founded project [INTERSTAT](https://cef-interstat.eu/).

## Installation and configurations

It is a Spring Boot application that by default has the TCP/IP port set to the value 8082. 
To change this configuration, simply change the value of the following configuration parameter in the 
**application.properties** file:
```yml
server.port = 8082
```
In this same file it is also possible to change the base path of the Idra application. To do this, 
it is necessary to modify the following parameter:
```yml
idra.basepath = http://localhost:8080
```

## Asynchronous notifications

The Orion Context Broker offers also an asynchronous notification mechanism: applications can subscribe to changes of context 
information so that they can be informed when something happens. This means the application does not need to continuously poll 
or repeat query requests.

It is possible to request the sending of notifications to a specific end-point, to be specified within the subscription.
To view all the subscriptions currently active in your Context Broker, you must send the following request:
```yml
curl contextBrokerUrl/ngsi-ld/v1/subscriptions/
```

Below are some examples of requests to the Context Broker to obtain information about the federated catalogues in Idra.

### Request for notification regarding the federation of a specific Catalogue

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

### Request for notification relating to the modification of the metadata of a Dataset (or Distribution) and to the addition of a new Dataset (or Distribution)

```yml
{
  "description": "Notify me on the creation or modification of a Dataset",
  "type": "Subscription",
  "entities": [{"type": "Dataset"}, {"type": "DistributionDCAT-AP"}],
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