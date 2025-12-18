package chatrealtime.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class ChatrealtimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatrealtimeApplication.class, args);
	}


	@Bean(name = "taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("AI-Executor-");
		executor.initialize();
		return executor;
	}

}
