import Messages.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.time.Duration;

public class OperatorActor extends AbstractActor {

    public String operatorName;
    private ActorRef queue;
    private ActorRef currentClient;

    public OperatorActor(String operatorName, ActorRef queue) {
        this.operatorName = operatorName;
        this.queue = queue;
        currentClient = null;
        waiting(new NoClientsMessage());
    }

    public static Props props(ActorRef queue, String operatorName) {
        return Props.create(OperatorActor.class, operatorName, queue);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NoClientsMessage.class, this::waiting)
                .match(ConnectClientAndOperatorMessage.class, this::serveClient)
                .build();
    }

    public void checkForClients() {
        if (currentClient == null) {
            System.out.println("Operator " + operatorName + " is looking for clients");
            queue.tell(new OperatorIdleMessage(operatorName), getSelf());
        }
    }

    public void waiting(NoClientsMessage message) {
        if (currentClient == null) {
            getContext().getSystem().scheduler().scheduleAtFixedRate(
                    Duration.ofMillis(1000),
                    Duration.ofMillis(5000),
                    this::checkForClients,
                    getContext().dispatcher());
        }
    }

    public void serveClient(ConnectClientAndOperatorMessage message) {
        currentClient = message.getClient();
        System.out.println("Operator " + operatorName + " is serving client " + currentClient.toString());
        getContext().getSystem().scheduler().scheduleOnce(
                Duration.ofMillis((int) (Math.random() * 5000) + 8000),
                this::clientServed,
                getContext().getDispatcher()
        );
    }
    public void clientServed() {
        currentClient = null;
        new ClientServedMessage(operatorName);
        waiting(new NoClientsMessage());
    }

    @Override
    public String toString() {
        return operatorName;
    }
}
