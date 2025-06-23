package ua.edu.networking.task4;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    private static final String CUSTOM_COOKIE_NAME = "MY_COOKIE";
    private final Map<String, MySession> sessions = new ConcurrentHashMap<>();

    @Override
    @SneakyThrows
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        MySession session = getSessionOrElseCreate(req, resp);
        String greeting = createGreeting(req, session);
        sendResponse(resp, greeting);
    }

    private String createGreeting(HttpServletRequest req, MySession session) {
        Optional<String> nameParam = Optional.ofNullable(req.getParameter("name"));
        if (session.get("name").isEmpty()) {
            nameParam.ifPresent(name -> session.put("name", name));
        }
        return nameParam
                .map(name -> "Hello, " + name + "!")
                .or(() -> session.get("name").map(obj -> "Hello, " + obj + "!"))
                .orElse("Hello!");
    }

    private void sendResponse(HttpServletResponse resp, String greeting) {
        try (PrintWriter writer = resp.getWriter()) {
            writer.println(greeting);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private MySession getSessionOrElseCreate(HttpServletRequest req, HttpServletResponse resp) {
        Optional<Cookie> sessionCookie = Optional.ofNullable(req.getCookies())
                .stream()
                .flatMap(Arrays::stream)
                .filter(cookie -> cookie.getName().equals(CUSTOM_COOKIE_NAME))
                .filter(cookie -> sessions.containsKey(cookie.getValue()))
                .findFirst();
        Cookie cookie = sessionCookie.orElse(createNewSession());
        if (!sessions.containsKey(cookie.getValue())) {
            sessions.put(cookie.getValue(), new MySession());
            resp.addCookie(cookie);
        }
        return sessions.get(cookie.getValue());
    }

    private Cookie createNewSession() {
        String sessionId = UUID.randomUUID().toString();
        return new Cookie(CUSTOM_COOKIE_NAME, sessionId);
    }
}
