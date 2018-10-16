package com.example.repro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.repro.ReproApplication.TestBus;

@SpringBootApplication
@EnableBinding(TestBus.class)
@RestController
public class ReproApplication {

    @Autowired
    private MessageChannel testOutput;

    @GetMapping("/test")
    public void test(@RequestParam("message") String message) {
        testOutput.send(MessageBuilder.withPayload(message).build());
    }

    @StreamListener(TestBus.TEST_INPUT)
    public synchronized void onTestTopicInput(String message) {
        System.out.println("******************");
        System.out.println("Received message : " + message);
        throw new RuntimeException("oooops");
    }

    public interface TestBus {

        String TEST_INPUT = "testInput";

        String TEST_OUTPUT = "testOutput";

        @Input(TEST_INPUT)
        MessageChannel testInput();

        @Output(TEST_OUTPUT)
        MessageChannel testOutput();
    }

    public static void main(String[] args) {
        SpringApplication.run(ReproApplication.class, args);
    }
}
