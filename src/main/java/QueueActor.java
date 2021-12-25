import Messages.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.LinkedList;

public class QueueActor extends AbstractActor {

    private LinkedList<ActorRef> waitingClients;

    public QueueActor() {
        waitingClients = new LinkedList<>();
    }

    public static Props props() {
        return Props.create(QueueActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClientCalledMessage.class, this::addClientToQueue)
                .match(OperatorIdleMessage.class, this::connectOperator)
                .match(EndCallMessage.class, this::removeClientFromQueue)
                .build();
    }

    public void addClientToQueue(ClientCalledMessage message) {
        System.out.println("Client "+ message.getName() + " called");
        waitingClients.add(getSender());
        getSender().tell(new ClientAddedToQueueMessage(), getSelf());
        System.out.println("QUEUE SIZE: " + waitingClients.size());
    }

    public void connectOperator(OperatorIdleMessage message) {
        if (waitingClients.isEmpty()) {
//            System.out.println("There are no clients for operator " + message.getName());
            getSender().tell(new NoClientsMessage(), getSelf());
        } else {
            ActorRef client = waitingClients.getFirst();
            waitingClients.remove(client);
            ConnectClientAndOperatorMessage connectClientAndOperatorMessage = new ConnectClientAndOperatorMessage(getSender(), client);
            client.tell(connectClientAndOperatorMessage, getSelf());
            getSender().tell(connectClientAndOperatorMessage, getSelf());
        }
    }

    public void removeClientFromQueue(EndCallMessage message) {
        System.out.println("Client " + message.getName() + " was removed from queue");
        waitingClients.remove(getSender());
    }
}
