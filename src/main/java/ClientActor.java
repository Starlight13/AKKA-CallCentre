import Messages.*;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.time.Duration;

public class ClientActor extends AbstractActor {

    private ActorRef operator;
    private String name;
    private ActorRef queue;

    public ClientActor(ActorRef queue, String name) {
        this.name = name;
        this.queue = queue;
        call();
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(ClientAddedToQueueMessage.class, this::waiting)
                .match(ConnectClientAndOperatorMessage.class, this::talkToOperator)
                .match(ClientServedMessage.class, this::endCall)
                .build();
    }

    public static Props props(ActorRef queue, String name) {
        return Props.create(ClientActor.class, queue, name);
    }

    public void call() {
        queue.tell(new ClientCalledMessage(name), getSelf());
    }

    public void waiting(ClientAddedToQueueMessage message) {
        System.out.println("Client " + name + " is waiting");
        getContext().getSystem().scheduler().scheduleOnce(
                Duration.ofMillis((int) (Math.random() * 2000) + 8000),
                this::recall,
                getContext().dispatcher());
    }

    public void recall() {
        if (operator == null) {
            System.out.println("Client " + name + " has ended the call");
            queue.tell(new EndCallMessage(name), getSelf());
            getContext().getSystem().scheduler().scheduleOnce(
                    Duration.ofMillis((int) (Math.random() * 2000) + 8000),
                    this::call,
                    getContext().dispatcher());
        }
    }

    public void talkToOperator(ConnectClientAndOperatorMessage message) {
        operator = message.getOperator();
    }

    public void endCall(ClientServedMessage message) {
        operator = null;
        getContext().getSystem().stop(getSelf());
    }

    @Override
    public String toString() {
        return name;
    }
}
