# Order Processing System

Order processing system is a combination of two services namely the **Order Manager** and 
**Order Processor**. It accepts multiple orders and processes them one after the other. It uses 
**Kafka** messaging queue to communicate between the two services.

## System Design
![System Design](/docs/userdoc/system_design.png)

### Order Manager

#### Endpoints
The Order Manager service serves http REST endpoints to manipulate orders.
- `POST /order` to create orders
- `GET /order/{orderId}` to fetch order details based on `orderId` and check the `OrderStatus`
- `GET /order` to fetch all order details

It persists the incoming orders and returns the user with `orderId`.

#### Order Producers
These orders are then processed by the background schedulers.
The schedulers place the order to the **Order Processor service** by
sending details to their respective `process-{type}-order-topic` kafka topic based
on `VehicleType`.

#### Feedback Consumers
Feedbacks sent from the Order Processor service is then consumed by
the FeedbackConsumer schedulers. These tasks update the current status of
each orders in the Database based on the feedback received.

### Order Processor
#### Endpoints
The Order Processor service seres http REST endpoints to manage the inventory.
- `POST /inventory` to upsert inventory details
- `GET /inventory` to check inventory's current status

#### Processor Engine
The Order processor engine is the core task that processes an incoming order.

It consists of the processing logic, Order Consumers and Feedback producers.

#### Order Consumers
The order consumers are scheduled background tasks that fetches placed orders
from the respective kafka topics.

#### Feedback Producers
The feedback producers send the feedback to the **Order Manager Service**
during the different stages of the process, thereby informing the current status 
of the order.

#### Processing Logic
The orders are moved to `PROCESSING` status when there is enough inventory to
accommodate the order. Then it will process the order after a random wait time
and move the order to `FINISHED` status. If there is not enough inventory
to process the current order, it holds at the current order until the inventory
is enough to process. No further orders will be processed till the existing
order is processed.

### Messaging System
The messaging system is built using Apache Kafka.
It holds the following topics:
- `process-car-order-topic`
- `process-truck-order-topic`
- `car-orders-feedback-topic`
- `truck-orders-feedback-topic`

The order processor topics are served with objects of `PlaceOrder` type.
The feedback topics transfer messages of type `OrderFeedback`.
By default, the consumer configs have auto commit turned off. The consumer classes
has to take care of polling and committing offsets.
Default replication and partition factor is `1`.

### Database
The system uses a `postgres` db to persist order details and inventory details.

## Software Architecture Design
The services follow `Clean Architecture`, extending into `Onion Ring` architecture with the below
layers (or components):
![Software Design](/docs/userdoc/software_design.png)

## Modules
This is a Mono Repo for both the **Order Manager** service and **Order Processor** Service.
It consists of three modules:
- order common
- order manager
- order processor

### Order Common
Order Common comprises the shared DTOs, Models and Abstract classes between
the two services. Both Order Manager and Order Processor modules depend on Order Common module.

It hosts the following abstract kafka consumers and producers:
- `AbstractKafkaConsumer` *Abstract consumer class to consumer from kafka topics*
- `AbstractScheduledKafkaConsumer` *Abstract bg task extending the abstract consumer that's run in fixed interval*
- `AbstractKafkaProducer` *Abstract producer class to write to kafka topics*
- `AbstractScheduledKafkaProducer` *Abstract bg task extending the abstract producer that's run in fixed intervals*

### Order Manager
This is the source root for the **Order Manger** service (OrderManagerApplication).
It comprises the following packages/files:
- `KafkaConfig` *Holds configurations for kafka topics, producers and consumers*
- `OrderController` *REST endpoint controllers*
- `DTOs` *Incoming and outgoing to DTOs carrying business logic there by hiding domain logic*
- `Model` *Domain models holder*
- `Repository` *Database connection layer*
- `Service` *Service layer between business and domain*
- `FeedbackConsumers` *Consumer tasks to read from Feedback topics*
- `OrderProducers` *Order producers to write to Process-order topics*

### Order Processor
This is the source root for the **Order Processor** service (OrderProcessorApplication).
It comprises the following packages/files:
- `KafkaConfig`, `Model`, `Repository`, `Service` *similar to order manager module*
- `InventoryController` *REST endpoint to do operations on inventory*
- `FeedbackProducers` *Order Feedback producers to write to Feedback topics*
- `ProcessorEngines` *Core business logic engine to receive orders, process orders and send feedback to update the status*

```Note: More details about individual classes can be found in the JavaDocs```

## Development details
The application is built and run in docker engine. The parent directory
holds the `docker-compose.yml` file which can be built and run by executing `docker-compose up` command
from the parent directory. Make sure you have a docker daemon running before executing the command.

5 containers will be spun up in total.
2 for each service, 1 for postgres db, 1 for kafka and 1 for zookeeper.

## MVP Details and Future enhancements
The MVP of the Order Processing System, delivers all the critical business functionalities.
All the above-mentioned functionalities are met in the MVP. Below are some of the key notables delivered:
- System design with respect to business use case
- Software design to develop maintainable code for long run with minimal refactoring and lose coupling
- Modular code to run as micro services
- Code abstraction adhering to SOLID principles to act as a base code for future development.
- Added class level JavaDoc for all modules for ease of understanding

The following are few of the known possible deliverables that can be considered for next releases:
- Adding database testing
- Adding E2E tests (maybe with cucumber framework)
- Adding metrics (maybe with graphite and grafana to have observability)
- Adding DAO layer to provide lose coupling between service layer and repository layer
- Add package and method level JavaDocs
- Better configuration of the Kafka systems to have scalability
- Add security layer for Authentication and authorisation