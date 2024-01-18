package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/count")
public class Controller {

    @PostMapping
    public Response generateData(@RequestBody CountRequest countRequest) {
        System.out.println("New request with count: " + countRequest.getCount());
        int count = countRequest.getCount();
        List<DataItem> data = new ArrayList<>();

        // Загрузка данных из CSV файлов, используя ресурсы проекта
        List<String> lastnames = loadFromCSV("lastname.csv");
        List<String> firstnames = loadFromCSV("firstname.csv");

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            DataItem item = new DataItem();
            // Генерация случайных фамилий и имен
            item.setLastname(lastnames.get(random.nextInt(lastnames.size())));
            item.setFirstname(firstnames.get(random.nextInt(firstnames.size())));
            item.setId(UUID.randomUUID().toString());
            data.add(item);
        }

        return new Response(data);
    }

    private List<String> loadFromCSV(String fileName) {
        List<String> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/" + fileName))))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                data.add(nextLine[0]); // Предполагаем, что в CSV файле только одна колонка
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
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
        private String lastname;
        private String firstname;
        private String id;

        // getters and setters

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    static class Response {
        private List<DataItem> data;

        public Response(List<DataItem> data) {
            this.data = data;
        }

        // getters and setters

        public List<DataItem> getData() {
            return data;
        }

        public void setData(List<DataItem> data) {
            this.data = data;
        }
    }
}
