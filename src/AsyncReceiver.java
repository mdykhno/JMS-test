import javax.jms.*;
import javax.naming.*;
import java.util.Hashtable;

//Обратите внимание, что класс реализует интерфей MessageListener
public class AsyncReceiver implements MessageListener{
    public AsyncReceiver(){
    }
    //Метод onMessage интерфейса MessageListener будет вызван
//Consumer'ом для обработки поступившего сообщения
    public void onMessage(Message message){
        try{
            TextMessage textMessage = (TextMessage)message;
            System.out.println("Got a message : "+textMessage.getText());
        }catch(JMSException jmse){
            jmse.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try{
            Hashtable env;
//            env = new Hashtable();
//            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.messaging.QueueConnectionFactory");
//            // On Unix, use file:///tmp instead of file:///C:/Temp
//            env.put(Context.PROVIDER_URL, "localhost:8080");
//            ConnectionFactory connFactory = new com.sun.messaging.ConnectionFactory();
            Context ctx = new InitialContext();
            QueueConnectionFactory qcf =
                    (QueueConnectionFactory)ctx.lookup( "MyConnectionFactory");
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
            QueueReceiver receiver = session.createReceiver(queue);
            con.start();
            AsyncReceiver async = new AsyncReceiver();
//Зарегистрировать новый AsyncReceiver обьект как
            //MessageListener дляreceiver
            receiver.setMessageListener(async);
            //А далее - делать что-либо, не ожидая сообщения.
            //Сообщение будет обработано, как только придет.
            //Для примера будем эмулировать бурную деятельность –
            //"спать"!
            Thread.sleep(100000000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}