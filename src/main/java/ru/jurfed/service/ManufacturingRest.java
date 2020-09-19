package ru.jurfed.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.jurfed.domain.Manufacturing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class ManufacturingRest {

    ScheduledExecutorService service = Executors.newScheduledThreadPool(20);

    @RequestMapping(value = "/manufacture", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

    public void sendPresentToOrder(@RequestBody Manufacturing manufacturing) {


        service.schedule(new CreatePresents(manufacturing), (int) Math.random() * 2000 + 200, TimeUnit.MILLISECONDS);

        System.out.println();

    }


    class CreatePresents implements Runnable{

        private Manufacturing manufacturing;

        public CreatePresents(Manufacturing manufacturing) {
            this.manufacturing = manufacturing;
        }

        @Override
        public void run() {
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = "http://localhost:" + 8091 + "/manufactured";
            try {
                URI uri = new URI(baseUrl);
                HttpEntity<Manufacturing> requestBody = new HttpEntity(manufacturing);
                restTemplate.postForEntity(uri,requestBody,Manufacturing.class);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }

}