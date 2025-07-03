package project;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GsonConverter {

    private static Gson gson = new Gson();

    // Метод, который принимает JSON объект и возвращает его копию, но с полями типа String
    public static JsonObject convertToStringJsonObject(JsonObject inputJson) {
        JsonObject result = new JsonObject();

        // Проходим по каждому полю входного объекта
        for (Map.Entry<String, com.google.gson.JsonElement> entry : inputJson.entrySet()) {
            String key = entry.getKey();
            com.google.gson.JsonElement value = entry.getValue();

            // Преобразуем значение в строку (если оно не строка)
            String stringValue = value.toString();

            // Добавляем поле в новый JsonObject
            result.addProperty(key, stringValue);
        }

        return result;
    }

    public static void main(String[] args) {
        String json1 = "{\n" +
                "  \"id\": \"f47ac10b-58cc-4372-a567-0e02b2c3d479\",\n" +
                "  \"title\": \"International Football Championship\",\n" +
                "  \"description\": \"An exciting football tournament for professionals from around the world.\",\n" +
                "  \"sport\": \"football\",\n" +
                "  \"type_tournament\": \"team\",\n" +
                "  \"type_group\": \"olympic\",\n" +
                "  \"matches_number\": 32,\n" +
                "  \"start_time\": \"2025-08-01T10:00:00Z\",\n" +
                "  \"created_at\": \"2025-07-02T12:00:00Z\",\n" +
                "  \"entry_cost\": 150.50,\n" +
                "  \"max_participants\": 64,\n" +
                "  \"registration_deadline\": \"2025-07-31T10:00:00Z\",\n" +
                "  \"place\": \"Stadium XYZ, City ABC\",\n" +
                "  \"organizer_id\": \"6d8a6bfe-e9b2-4875-90c2-78e1ab263489\"\n" +
                "}";

        String json2 = "{\n" +
                "  \"id\": \"d20b3171-0f99-4128-b6c1-fb23e2df3f60\",\n" +
                "  \"title\": \"Chess Grandmaster Tournament\",\n" +
                "  \"description\": \"A prestigious chess tournament bringing together the world's top grandmasters.\",\n" +
                "  \"sport\": \"chess\",\n" +
                "  \"type_tournament\": \"solo\",\n" +
                "  \"type_group\": \"round_robin\",\n" +
                "  \"matches_number\": 16,\n" +
                "  \"start_time\": \"2025-09-15T14:00:00Z\",\n" +
                "  \"created_at\": \"2025-07-10T09:30:00Z\",\n" +
                "  \"entry_cost\": 200.00,\n" +
                "  \"max_participants\": 16,\n" +
                "  \"registration_deadline\": \"2025-09-14T14:00:00Z\",\n" +
                "  \"place\": \"Grand Hall, New York City\",\n" +
                "  \"organizer_id\": \"cbe430f1-2b4b-4ec7-bcb3-ff9f40182df5\"\n" +
                "}";

        String json3 = "{\n" +
                "  \"id\": \"12a9e7cf-b97a-4b0e-a5f0-95e34f8d5399\",\n" +
                "  \"title\": \"Ultimate Boxing Championship\",\n" +
                "  \"description\": \"A high-stakes boxing competition where fighters from different countries compete for the title.\",\n" +
                "  \"sport\": \"boxing\",\n" +
                "  \"type_tournament\": \"team\",\n" +
                "  \"type_group\": \"swiss\",\n" +
                "  \"matches_number\": 24,\n" +
                "  \"start_time\": \"2025-11-01T18:00:00Z\",\n" +
                "  \"created_at\": \"2025-06-20T15:45:00Z\",\n" +
                "  \"entry_cost\": 100.00,\n" +
                "  \"max_participants\": 48,\n" +
                "  \"registration_deadline\": \"2025-10-31T18:00:00Z\",\n" +
                "  \"place\": \"Arena, Las Vegas\",\n" +
                "  \"organizer_id\": \"a876b1a2-9a56-49d1-8ff3-045a64ad2350\"\n" +
                "}";

        // Преобразуем строки в JsonObjects
        JsonObject jsonObject1 = gson.fromJson(json1, JsonObject.class);
        JsonObject jsonObject2 = gson.fromJson(json2, JsonObject.class);
        JsonObject jsonObject3 = gson.fromJson(json3, JsonObject.class);

        // Преобразуем их в JSON объекты с полями типа String
        JsonObject outputJson1 = convertToStringJsonObject(jsonObject1);
        JsonObject outputJson2 = convertToStringJsonObject(jsonObject2);
        JsonObject outputJson3 = convertToStringJsonObject(jsonObject3);

        // Печатаем преобразованные объекты, используя .toString() для корректного вывода
        System.out.println("Converted JSON 1: " + outputJson1.toString());
        System.out.println("Converted JSON 2: " + outputJson2.toString());
        System.out.println("Converted JSON 3: " + outputJson3.toString());
    }
}
