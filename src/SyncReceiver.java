import javax.jms.*;
import javax.naming.*;
public class SyncReceiver {
    public static void main(String[] args) {
        try{
            Context ctx = new InitialContext();
            QueueConnectionFactory qcf =
                    (QueueConnectionFactory)ctx.lookup("MyConnectionFactory");
            QueueConnection con = qcf.createQueueConnection();
            QueueSession session =
                    con.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
            Queue queue = null;
            try{
                queue = (Queue)ctx.lookup("MyQueue");
            }catch(NameNotFoundException nnfe){
                queue = session.createQueue("MyQueue");
                ctx.bind("MyQueue", queue);
            }
            // Создадим Consumer
            QueueReceiver receiver = session.createReceiver(queue);
            // Активизируем Connection
            con.start();
//Мы готовы принимать сообщения
            for(;;){
//Обратите внимание на использование метода receive()
                TextMessage textMessage = (TextMessage)receiver.receive();
                System.out.println("Got a message :"+textMessage.getText());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}