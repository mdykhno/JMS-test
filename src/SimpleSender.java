import javax.jms.*;
import javax.naming.*;
import java.util.Hashtable;

public class SimpleSender{
    public static void main(String args[]){
        try {
//Сначала найдем сервис JNDI
            Hashtable env;
//            env = new Hashtable();
//            env.put(Context.INITIAL_CONTEXT_FACTORY, "javax.jms.QueueConnectionFactory");
//            // On Unix, use file:///tmp instead of file:///C:/Temp
//            env.put(Context.PROVIDER_URL, "localhost:8080");
            Context ctx = new InitialContext();
            //Теперь найдем ConnectionFactory с помощью JNDI
            //Имя, с которым зарегистрирован
// стандартный ConnectionFactory в дереве
            // JNDIзависит от реализации –
// например, в случае WebLogic Server это
            // weblogic.jms.ConnectionFactory
            QueueConnectionFactory qcf =
                    (QueueConnectionFactory)ctx.lookup(
                            "MyConnectionFactory");
            //А теперь создадим Connection
            QueueConnection con = qcf.createQueueConnection();
            //Создадим Session со следующими свойствами:
            //а)посылка сообщений не является частью транзакции
            //б)подтверждение о получении сообщения осуществляет JMS
            //автоматически
            QueueSession session =
                    con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            //Найдем Destination с помощью JNDI. Если еще не существует
            // (не зарегистрирована в JNDI) – создадим и зарегистрируем
            Queue queue = null;
            try{
                queue = (Queue)ctx.lookup("MyQueue");
            }catch(NameNotFoundException nnfe){
                queue = session.createQueue("MyQueue");
                ctx.bind("MyQueue",queue);
            }
//Простейшее текстовое сообщение
            TextMessage textMessage =
                    session.createTextMessage();
            //Создадим Producer
            QueueSender sender =
                    session.createSender(queue);
            //Активизируем Connection
            con.start();
            //Теперь мы готовы посылать сообщения
            //Будем посылать наше сообщение каждую секунду
            //или тот интервал, который указан в первом аргументе
            long sendInterval;
            try{
                sendInterval = Long.parseLong(args[0]);
            }catch(Exception rae){
                sendInterval = 1000;
            }
            //Пошлем, например, тысячу сообщений
            for(int i=0;i<1000;i++){
                //Создадим простейшее текстовое сообщение
                textMessage.setText("Hi! It's my message number "+i);
                //И пошлем его
                sender.send(textMessage);
                try{
                    Thread.sleep(sendInterval);
                }catch(Exception se){
                    System.out.println("Got an exception "+se.getMessage());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

