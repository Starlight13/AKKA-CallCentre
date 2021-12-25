import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("test-system");

        ActorRef queue = system.actorOf(QueueActor.props(), "queue");

        ActorRef operator1 = system.actorOf(OperatorActor.props(queue, "1"));
        ActorRef operator2 = system.actorOf(OperatorActor.props(queue, "2"));
//        ActorRef operator3 = system.actorOf(OperatorActor.props(queue, "3"));

        ClientProducer clientProducer = new ClientProducer(queue, system);
        System.out.println("123456");
        clientProducer.start();
    }
}
