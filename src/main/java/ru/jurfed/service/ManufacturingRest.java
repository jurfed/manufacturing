package ru.jurfed.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class ManufacturingRest {

    ScheduledExecutorService service = Executors.newScheduledThreadPool(20);
    private static final Logger logger = LogManager.getLogger(ManufacturingRest.class);

    @RequestMapping(value = "/manufacturing", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void getOrder(@RequestBody Manufacturing manufacturing) {
        service.schedule(new CreatePresents(manufacturing), (int) Math.random() * 2000 + 200, TimeUnit.MILLISECONDS);
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
                logger.warn("The goods produced");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }


    @EventListener(ApplicationStartedEvent.class)
    public void checkAvailableAfterStartUp() {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + 8091 + "/manufacturingStarts";
        try {
            URI uri = new URI(baseUrl);
            HttpEntity<Object> requestBody = new HttpEntity(new Object());
            restTemplate.getForEntity(uri,Object.class);
        } catch (Exception e) {
            logger.warn("Present-system server is not running");
        }

    }

}
