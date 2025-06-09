package ua.edu.networking.task2;

public class Main {
    public static void main(String[] args) {
        RequestSender requestSender = new RequestSender("localhost", 8080);
        requestSender.getRequest();
        requestSender.postRequest(new Note("Abra Cadabra", "DS"));
    }
}
