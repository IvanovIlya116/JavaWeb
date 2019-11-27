package ru.itis.web.servlets;

import lombok.SneakyThrows;
import ru.itis.web.dto.SignUpForm;
import ru.itis.web.services.UsersService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(value = "/signUp")
public class SignUpServlet extends HttpServlet {
    // сервлет не знает о том, как реализован сервис. Это нужно для возможности изменения реализации интерефейса
    // чтобы не перерписывать сервлет
    private UsersService usersService;

    @SneakyThrows
    @Override
    public void init(ServletConfig config) throws ServletException {
        usersService = (UsersService) config.getServletContext().getAttribute("usersService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SignUpForm form = SignUpForm.builder()
                .firstName(req.getParameter("firstName"))
                .lastName(req.getParameter("lastName"))
                .login(req.getParameter("login"))
                .password(req.getParameter("password"))
                .build();

        usersService.signUp(form);
        // после регистрации перенаправляем на страницу аутентификации
        resp.sendRedirect("/signIn");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // позволяет отдавать страницу как html
        req.getRequestDispatcher("WEB-INF/jsp/SignUp.jsp").forward(req, resp);
    }
}
