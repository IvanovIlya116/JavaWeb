package ru.itis.web.repositories.jdbc;


import ru.itis.web.models.User;
import ru.itis.web.models.UserRole;
import ru.itis.web.repositories.UsersRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsersRepositoryJdbcImpl implements UsersRepository {
    private Connection connection;
    private Map<Long, User> userMap;

    static RowMapper<User> baseUserRowMapper = row ->
            User.builder()
                    .id(row.getLong("id"))
                    .login(row.getString("login"))
                    .password(row.getString("password"))
                    .firstName(row.getString("first_name"))
                    .lastName(row.getString("last_name"))
                    .age(row.getInt("age"))
                    .role(UserRole.valueOf(row.getString("role")))
                    .phone(row.getString("phone"))
                    .email(row.getString("email"))
                    .build();


    // language=SQL
    private static final String SQL_INSERT_USER = "insert into service_user (first_name, last_name, age, phone, email, login, password, role) values (?, ?, ?, ?, ?, ?, ?,?);";
    // language=SQL
    private static final String SQL_FIND_BY_LOGIN = "select * from service_user where login = ?";
    // language=SQL
    private static final String SQL_FIND_ALL = "select * from service_user";


    public UsersRepositoryJdbcImpl(Connection connection) {

        this.connection = connection;
    }

    @Override
    public Optional<User> findOneById(Long id) {
        return Optional.empty();
    }

    @Override
    public User save(User model) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, model.getFirstName());
            statement.setString(2, model.getLastName());
            statement.setObject(3, model.getAge());
            statement.setString(4, model.getPhone());
            statement.setString(5, model.getEmail());
            statement.setString(6, model.getLogin());
            statement.setString(7, model.getPassword());
            statement.setString(8, model.getRole().toString());

            // сколько строк обновилось
            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) throw new IllegalArgumentException("Ничего не обновилось");

            // получили все сгенерерированные базой ключи
            // С помощью итератора ResultSet проходимся по базе данных
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) model.setId(generatedKeys.getLong("id"));
            else throw new IllegalArgumentException("Не смогли получить id");
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return model;
    }

    @Override
    public void update(User model) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<User> findAll() {
        try {
            List<User> users = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                User user = baseUserRowMapper.mapRow(resultSet);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_LOGIN);
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) return Optional.of(baseUserRowMapper.mapRow(resultSet));
            else return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
