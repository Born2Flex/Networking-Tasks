package ua.edu.networking.task3;

public class Main {
    public static void main(String[] args) throws Exception {
        NasaClient client = new NasaClient("ceqfVzm9Z8oEuvmtWqpeyfFfCsF3NUyUOW2xmXGB");
        client.findLargestPhoto();
    }
}
