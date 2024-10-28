package anh.nguyen.alovestory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import anh.nguyen.alovestory.services.UserService;

@SpringBootApplication
public class AlovestoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlovestoryApplication.class, args);
	}

}
