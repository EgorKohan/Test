package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.util.JdbcConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.netcracker.cats.dao.sql.SqlQueries.*;

public class CatDaoJdbcImpl implements CatDao {

    private final Connection CONNECTION = JdbcConnectionUtil.getInstance().getConnection();

    @Override
    public Cat getById(Long id) throws SQLException {
        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_SELECT_CAT_BY_ID)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapCat(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<Cat> getByAge(int age) throws SQLException {
        final List<Cat> cats = new ArrayList<>();
        try (final PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_SELECT_CATS_BY_AGE)) {
            preparedStatement.setLong(1, age);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cats.add(mapCat(resultSet));
                }
            }

        }

        return cats;
    }

    @Override
    public List<Cat> getAll() throws SQLException {
        final List<Cat> cats = new ArrayList<>();
        try (Statement statement = CONNECTION.createStatement()) {

            try (final ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_CATS)) {
                while (resultSet.next()) {
                    cats.add(mapCat(resultSet));
                }
            }

        }

        return cats;
    }

    @Override
    public Cat create(Cat cat) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     CONNECTION.prepareStatement(SQL_INSERT_CAT, Statement.RETURN_GENERATED_KEYS)) {

            mapBasicCatParameters(cat, preparedStatement);

            final int result = preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    return getById(id);
                }
                return null;
            }

        }
    }

    @Override
    public Cat update(Cat cat) throws SQLException {

        try (final PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_UPDATE_CAT_BY_ID)) {
            mapBasicCatParameters(cat, preparedStatement);
            preparedStatement.setLong(7, cat.getId());

            final int result = preparedStatement.executeUpdate();

            return getById(cat.getId());
        }
    }

    private void mapBasicCatParameters(Cat cat, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setString(2, cat.getGender().toString());
        preparedStatement.setString(3, cat.getColor());
        preparedStatement.setInt(4, cat.getAge());

        preparedStatement.setLong(5, (cat.getFather() != null) ?
                cat.getFather().getId() :
                0);
        preparedStatement.setLong(6, (cat.getMother() != null) ?
                cat.getMother().getId() :
                0);
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        int result;

        try (final PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_DELETE_CAT_BY_ID)) {
            preparedStatement.setLong(1, id);
            result = preparedStatement.executeUpdate();
        }

        return result != 0;
    }

    @Override
    public List<Cat> findCatsByName(String name) throws SQLException {
        List<Cat> cats = new ArrayList<>();

        try (final PreparedStatement preparedStatement =
                     CONNECTION.prepareStatement(SQL_SELECT_CATS_BY_NAME_SUBSTRING)) {
            String pattern = "%" + name + "%";
            preparedStatement.setString(1, pattern);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cats.add(mapCat(resultSet));
                }
            }

        }

        return cats;
    }

    private Cat mapCat(ResultSet resultSet) throws SQLException {
        Cat cat = createCatFromResultSet(resultSet);

        Long fatherId = resultSet.getLong("father_id");
        Long motherId = resultSet.getLong("mother_id");
        Cat father = getById(fatherId);
        Cat mother = getById(motherId);
        if (father != null) father.getChildren().add(cat);
        if (mother != null) mother.getChildren().add(cat);
        cat.setFather(father);
        cat.setMother(mother);

        final List<Cat> childrenFromResultSet = createChildrenFromResultSet(cat);
        cat.getChildren().addAll(childrenFromResultSet);

        return cat;
    }

    // children dont add FIXME
    private List<Cat> createChildrenFromResultSet(Cat cat) throws SQLException {
        String query = (cat.getGender().equals(Gender.MALE)) ?
                SQL_SELECT_FATHER_CHILD :
                SQL_SELECT_MOTHER_CHILD;

        List<Cat> cats = new ArrayList<>();

        try (final PreparedStatement preparedStatement = CONNECTION.prepareStatement(query)) {
            preparedStatement.setLong(1, cat.getId());

            try (final ResultSet children = preparedStatement.executeQuery()) {
                while (children.next()) {
                    final Cat catFromResultSet = createCatFromResultSet(children);

                    if ((cat.getGender().equals(Gender.MALE))) {
                        catFromResultSet.setFather(cat);
                    } else {
                        catFromResultSet.setMother(cat);
                    }

                    final List<Cat> childrenFromResultSet = createChildrenFromResultSet(catFromResultSet);
                    catFromResultSet.getChildren().addAll(childrenFromResultSet);

                    cats.add(
                            catFromResultSet
                    );
                }
            }

        }

        return cats;
    }

    private Cat createCatFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Integer age = resultSet.getInt("age");
        String color = resultSet.getString("color");
        Gender gender = Gender.valueOf(resultSet.getString("gender"));

        return Cat.builder()
                .id(id)
                .name(name)
                .age(age)
                .color(color)
                .gender(gender)
                .build();
    }

}
