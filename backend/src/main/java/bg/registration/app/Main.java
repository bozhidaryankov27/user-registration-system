
package bg.registration.app;

import bg.registration.filter.CorsFilter;
import bg.registration.http.handlers.*;
import bg.registration.repository.SQLUserRepository;
import bg.registration.repository.UserRepository;
import bg.registration.service.CaptchaService;
import bg.registration.service.SessionManager;
import bg.registration.service.UserService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        UserRepository userRepository = new SQLUserRepository();
        CaptchaService captchaService = new CaptchaService();
        SessionManager sessionManager = new SessionManager();
        UserService userService = new UserService(userRepository);

        HttpContext captcha = server.createContext("/api/captcha", new CaptchaHandler(captchaService));
        HttpContext login = server.createContext("/api/login", new LoginHandler(sessionManager, userService));
        HttpContext register = server.createContext("/api/register", new RegisterHandler(userService, captchaService));
        HttpContext profile = server.createContext("/api/profile", new ProfileHandler(sessionManager, userRepository, userService));
        HttpContext logout = server.createContext("/api/logout", new LogoutHandler(sessionManager));

        CorsFilter cors = new CorsFilter();

        captcha.getFilters().add(cors);
        login.getFilters().add(cors);
        register.getFilters().add(cors);
        profile.getFilters().add(cors);
        logout.getFilters().add(cors);

        server.start();

        System.out.println("Server is started at http://localhost:8080!");
    }
}

