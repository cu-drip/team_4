package project;

public class Main {
    public static void main(String[] args) {
        // Запуск HTTP-сервера в отдельном потоке
        new Thread(() -> {
            try {
                // Запуск HTTP-сервера
                HttpServerApp.startHttpServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        GsonConverter.main(args);
    }
}
