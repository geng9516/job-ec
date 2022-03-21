package con.chin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling//定時任務を実行のため
public class Application {
    public static void main(String args[]){
        SpringApplication.run(Application.class, args);
    }
}
