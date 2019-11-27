package ru.itis.web.listeners;

import lombok.SneakyThrows;
import ru.itis.web.repositories.CookieValuesRepository;
import ru.itis.web.repositories.jdbc.CookieValuesRepositoryJdbcImpl;
import ru.itis.web.repositories.UsersRepository;
import ru.itis.web.repositories.jdbc.UsersRepositoryJdbcImpl;
import ru.itis.web.services.UsersService;
import ru.itis.web.services.UsersServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ComponentsContainer implements ServletContextListener {

    // создается 1 объект usersService, которым пользуются все сервлеты
    @SneakyThrows
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties properties = new Properties();
        ServletContext context = sce.getServletContext();
        String fullPath = context.getRealPath("/WEB-INF/db.properties");
        properties.load(new FileReader(fullPath));
        // загружаем класс в JVM
        Class.forName(properties.getProperty("db.driver"));
        Connection connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.username"), properties.getProperty("db.password"));
        UsersRepository usersRepository = new UsersRepositoryJdbcImpl(connection);
        CookieValuesRepository cookieValuesRepository = new CookieValuesRepositoryJdbcImpl(connection);
        UsersService usersService = new UsersServiceImpl(usersRepository, cookieValuesRepository);
        // подключились один раз, положили в контекст сервлетов
        sce.getServletContext().setAttribute("usersService", usersService);
    }
}
