import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ClientProducer extends Thread {

    private ActorSystem system;
    private ActorRef queue;

    public ClientProducer(ActorRef queue, ActorSystem system){
        this.queue = queue;
        this.system = system;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            try {
                System.out.println("CLient created");
                i++;
                Thread.sleep((int) (Math.random() * 3000) + 3000);
                system.actorOf(ClientActor.props(queue, "Client" + i), "client_" + i);
            } catch (InterruptedException e){}
        }
    }
}
