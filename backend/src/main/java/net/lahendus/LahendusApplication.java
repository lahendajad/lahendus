package net.lahendus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@SpringBootApplication
public class LahendusApplication {

    public static void main(String[] args) {
        SpringApplication.run(LahendusApplication.class, args);
    }

    @RequestMapping("/resource")
    public String home() {
        return "Hello world!";
    }

}
