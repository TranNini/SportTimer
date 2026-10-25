package sporttimer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Starts the app on http://localhost:8082. It only serves the files in static/;
// the timer runs in the browser and keeps working offline (sw.js).
@SpringBootApplication
public class SportTimerApp {
    public static void main(String[] args) {
        SpringApplication.run(SportTimerApp.class, args);
    }
}
