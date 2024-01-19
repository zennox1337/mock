package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    // Инициализация логгера с использованием SLF4J
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @PostMapping
    public Response generateData(@RequestBody CountRequest countRequest) {
        // Запись информации о запросе в лог
        logger.info("New request with count: {}", countRequest.getCount());

        // Получение значения из запроса
        int count = countRequest.getCount();

        // Инициализация списка для хранения сгенерированных данных
        List<DataItem> data = new ArrayList<>();

        // Загрузка данных из CSV файлов, используя ресурсы проекта
        List<String> lastnames = loadFromCSV("lastname.csv");
        List<String> firstnames = loadFromCSV("firstname.csv");

        // Инициализация генератора случайных чисел
        Random random = new Random();

        // Генерация данных в количестве, указанном в запросе
        for (int i = 0; i < count; i++) {
            DataItem item = new DataItem();

            // Выбор случайных фамилий и имен из загруженных списков
            item.setLastname(lastnames.get(random.nextInt(lastnames.size())));
            item.setFirstname(firstnames.get(random.nextInt(firstnames.size())));

            // Генерация уникального идентификатора
            item.setId(UUID.randomUUID().toString());

            // Добавление сгенерированных данных в список
            data.add(item);
        }

        // Возврат ответа с сгенерированными данными
        return new Response(data);
    }

    // Метод для загрузки данных из CSV файла
    private List<String> loadFromCSV(String fileName) {
        List<String> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/" + fileName))))) {
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                data.add(nextLine[0]);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error loading data from CSV: {}", e.getMessage());
            // Дополнительная обработка ошибки
        } catch (NullPointerException e) {
            logger.error("Error loading data from CSV: Resource not found");
            // Обработка ситуации, когда ресурс не найден
        }

        return data;
    }

    // Внутренний класс, представляющий запрос с количеством запрашиваемых данных
    static class CountRequest {
        private int count;

        // Геттер и сеттер для количества
        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    // Внутренний класс, представляющий элемент данных
    static class DataItem {
        private String lastname;
        private String firstname;
        private String id;

        // Геттеры и сеттеры для фамилии, имени и идентификатора
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

    // Внутренний класс, представляющий ответ с сгенерированными данными
    static class Response {
        private List<DataItem> data;

        // Конструктор для инициализации ответа с данными
        public Response(List<DataItem> data) {
            this.data = data;
        }

        // Геттеры и сеттеры для списка данных
        public List<DataItem> getData() {
            return data;
        }

        public void setData(List<DataItem> data) {
            this.data = data;
        }
    }
}