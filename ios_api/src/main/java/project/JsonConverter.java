package project;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonConverter {

    private static Gson gson = new Gson();

    // Метод, который преобразует строку в camelCase
    private static String toCamelCase(String input) {
        StringBuilder camelCaseString = new StringBuilder();
        String[] parts = input.split("_");
        camelCaseString.append(parts[0].toLowerCase()); // Первое слово остается в нижнем регистре

        for (int i = 1; i < parts.length; i++) {
            // Каждую следующую часть делаем с заглавной буквы
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase());
            camelCaseString.append(parts[i].substring(1).toLowerCase());
        }

        return camelCaseString.toString();
    }

    // Метод, который принимает JSON объект и возвращает его копию с полями в camelCase и значениями в String
    public static JsonObject convertToStringJsonObject(JsonObject inputJson) {
        JsonObject result = new JsonObject();

        // Проходим по каждому полю входного объекта
        for (Map.Entry<String, com.google.gson.JsonElement> entry : inputJson.entrySet()) {
            String key = entry.getKey();
            com.google.gson.JsonElement value = entry.getValue();

            // Преобразуем имя поля в camelCase
            String camelCaseKey = toCamelCase(key);

            // Преобразуем значение в строку (без лишних кавычек)
            String stringValue = value.isJsonPrimitive() && value.getAsJsonPrimitive().isString() ? value.getAsString() : value.toString();

            // Добавляем поле в новый JsonObject
            result.addProperty(camelCaseKey, stringValue);
        }

        return result;
    }
}
