package org.example;

import com.opencsv.CSVReader;
import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/count")
public class Controller {

    private static final List<String> lastnames;
    private static final List<String> firstnames;

    static {
        // Вызываем один раз при загрузке класса
        lastnames = loadFromCSV("lastname.csv");
        firstnames = loadFromCSV("firstname.csv");
    }

    @Bean
    public MeterBinder processMemoryMetrics() {
        return new ProcessMemoryMetrics();
    }

    @Bean
    public MeterBinder processThreadMetrics() {
        return new ProcessThreadMetrics();
    }

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @PostMapping
    public Response generateData(@RequestBody CountRequest countRequest) {
//        logger.info("New request with count: {}", countRequest.getCount());
        return new Response(generateRandomData(countRequest.getCount()));
    }

    static private List<String> loadFromCSV(String fileName) {
        List<String> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(Objects.requireNonNull(Controller.class.getResourceAsStream("/" + fileName))))) {
            reader.forEach(line -> data.add(line[0]));
        } catch (Exception e) {
            logger.error("Error loading data from CSV: {}", e.getMessage());
        }

        return data;
    }

    private String getRandomElement(List<String> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

    private List<DataItem> generateRandomData(int count) {
        List<DataItem> data = new ArrayList<>();

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            data.add(new DataItem(getRandomElement(lastnames, random), getRandomElement(firstnames, random), UUID.randomUUID().toString()));
        }

        return data;
    }

    static class CountRequest {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    static class DataItem {
        private final String lastname;
        private final String firstname;
        private final String id;

        public DataItem(String lastname, String firstname, String id) {
            this.lastname = lastname;
            this.firstname = firstname;
            this.id = id;
        }

        public String getLastname() {
            return lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getId() {
            return id;
        }
    }

    static class Response {
        private final List<DataItem> data;

        public Response(List<DataItem> data) {
            this.data = data;
        }

        public List<DataItem> getData() {
            return data;
        }
    }
}
