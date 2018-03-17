package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
//        try {
//            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-bean.xml" });
//        } catch (Throwable e) {
//            System.out.println(e);
//        }
   }

}


