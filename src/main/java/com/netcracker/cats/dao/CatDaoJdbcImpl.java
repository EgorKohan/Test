package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.util.JdbcConnectionUtil;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CatDaoJdbcImpl implements CatDao {

    private final Connection CONNECTION = JdbcConnectionUtil.getConnection();

    //language=SQL
    private static final String SQL_SELECT_ALL_CATS =
            "SELECT * FROM netcracker.cats";
    //language=SQL
    private static final String SQL_FIND_CAT_BY_ID =
            "SELECT * FROM netcracker.cats WHERE id = ?";
    //language=SQL
    private static final String SQL_FIND_CATS_BY_AGE =
            "SELECT * FROM netcracker.cats WHERE age = ?";
    //language=SQL
    private static final String SQL_INSERT_CAT_INTO_CATS =
            "INSERT INTO netcracker.cats(name, gender, color, age) VALUE (?,?,?,?)";
    //language=SQL
    private static final String SQL_INSERT_CAT_INTO_PARENTS =
            "INSERT INTO netcracker.parents(child_id, father_id, mother_id) VALUE (?,?,?)";
    //language=SQL
    private static final String SQL_DELETE_CAT_BY_ID =
            "DELETE FROM netcracker.cats WHERE id = ?";
    //language=SQL
    private static final String SQL_UPDATE_CAT_BY_ID =
            "UPDATE netcracker.cats SET name = ?, gender = ?, color = ?, age = ? WHERE id = ?";
    //language=SQL
    private static final String SQL_UPDATE_PARENT_BY_ID =
            "UPDATE  netcracker.parents SET father_id = ?, mother_id = ? WHERE child_id = ?";
    //language=SQL
    private static final String SQL_FIND_PARENTS_ID =
            "SELECT * FROM netcracker.parents WHERE child_id = ?";
    //language=SQL
    private static final String SQL_FIND_CHILDREN_ID_FOR_FATHER =
            "SELECT  * FROM netcracker.parents WHERE father_id = ?";
    //language=SQL
    private static final String SQL_FIND_CHILDREN_ID_FOR_MOTHER =
            "SELECT  * FROM netcracker.parents WHERE mother_id = ?";
    //language=SQL
    private static final String SQL_FILTER_CATS_BY_NAME =
            "SELECT * FROM netcracker.cats " +
                    "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "WHERE cats.name LIKE ?";

    @Override
    public Cat getById(Long id) throws SQLException {
        //FIXME закрывать statement
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return createCat(resultSet);
        }
        return null;
    }

    @Override
    public List<Cat> getByAge(int age) throws SQLException {
        List<Cat> cats = new ArrayList<>();
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_CATS_BY_AGE);
        preparedStatement.setInt(1, age);
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            cats.add(
                    createCat(resultSet)
            );
        }
        return cats;
    }

    @Override
    public List<Cat> getAll() throws SQLException {
        final ResultSet resultSet = CONNECTION.createStatement().executeQuery(SQL_SELECT_ALL_CATS);
        final List<Cat> cats = new ArrayList<>();

        while (resultSet.next()) {
            cats.add(
                    createCat(resultSet)
            );
        }
        return cats;
    }

    @Override
    public Cat create(@NonNull Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.
                prepareStatement(SQL_INSERT_CAT_INTO_CATS, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, cat.getName());
        preparedStatement.setString(2, cat.getGender().toString());
        preparedStatement.setString(3, cat.getColor());
        preparedStatement.setInt(4, cat.getAge());

        final int result = preparedStatement.executeUpdate();

        Cat newCat = null;

        final ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            long id = generatedKeys.getLong(1);

            PreparedStatement preparedStatement1 = CONNECTION.prepareStatement(SQL_INSERT_CAT_INTO_PARENTS);
            preparedStatement1.setLong(1, id);
            if (cat.getFather() != null) {
                preparedStatement1.setLong(2, cat.getFather().getId());
            } else {
                preparedStatement1.setNull(2, Types.INTEGER);
            }
            if (cat.getMother() != null) {
                preparedStatement1.setLong(3, cat.getMother().getId());
            } else {
                preparedStatement1.setNull(3, Types.INTEGER);
            }
            preparedStatement1.executeUpdate();
            //присваивать id
            newCat = getById(id);
        }

        return newCat;
    }

    @Override
    public Cat update(@NonNull Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_UPDATE_CAT_BY_ID);
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setString(2, cat.getGender().toString());
        preparedStatement.setString(3, cat.getColor());
        preparedStatement.setInt(4, cat.getAge());
        preparedStatement.setLong(5, cat.getId());

        preparedStatement.executeUpdate();

        PreparedStatement updateParent = CONNECTION.prepareStatement(SQL_UPDATE_PARENT_BY_ID);
        PreparedStatement createParents = CONNECTION.prepareStatement(SQL_INSERT_CAT_INTO_PARENTS);

        if (cat.getFather() != null) {
            updateParent.setLong(1, cat.getFather().getId());
            createParents.setLong(2, cat.getFather().getId());
        } else {
            updateParent.setNull(1, Types.INTEGER);
            createParents.setNull(2, Types.INTEGER);
        }
        if (cat.getMother() != null) {
            updateParent.setLong(2, cat.getMother().getId());
            createParents.setLong(3, cat.getMother().getId());
        } else {
            updateParent.setNull(2, Types.INTEGER);
            createParents.setNull(3, Types.INTEGER);
        }
        updateParent.setLong(3, cat.getId());
        createParents.setLong(1, cat.getId());

        if (updateParent.executeUpdate() == 0) {
            createParents.executeUpdate();
        }

        return getById(cat.getId());
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_DELETE_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        return !preparedStatement.execute();
    }

    @Override
    public List<Cat> findCatsByName(String name) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FILTER_CATS_BY_NAME);
        preparedStatement.setString(1, "%" + name + "%");
        final ResultSet resultSet = preparedStatement.executeQuery();
        List<Cat> cats = new ArrayList<>();
        while (resultSet.next()) {
            cats.add(
                    createCat(resultSet)
            );
        }
        return cats;
    }

    private Cat mapCat(@NonNull ResultSet resultSet) throws SQLException {
        return Cat.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .age(resultSet.getInt("age"))
                .color(resultSet.getString("color"))
                .gender(Gender.valueOf(resultSet.getString("gender")))
                .build();
    }

    private void bindParentsToCat(@NonNull Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_PARENTS_ID);
        preparedStatement.setLong(1, cat.getId());
        final ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Long fatherId = resultSet.getLong("father_id");
            Long motherId = resultSet.getLong("mother_id");

            Cat father = mapCatById(fatherId);
            Cat mother = mapCatById(motherId);

            cat.setFather(father);
            cat.setMother(mother);
        }
    }

    private void bindChildrenToCat(@NonNull Cat cat) throws SQLException {
        List<Cat> children = new ArrayList<>();
        PreparedStatement preparedStatement = cat.getGender().equals(Gender.MALE) ?
                CONNECTION.prepareStatement(SQL_FIND_CHILDREN_ID_FOR_FATHER) :
                CONNECTION.prepareStatement(SQL_FIND_CHILDREN_ID_FOR_MOTHER);
        preparedStatement.setLong(1, cat.getId());
        final ResultSet resultSet = preparedStatement.executeQuery();

        long childId;
        while (resultSet.next()) {
            childId = resultSet.getLong("child_id");
            children.add(
                    mapCatById(childId)
            );
        }

        cat.getChildren().addAll(children);
    }

    private Cat createCat(@NonNull ResultSet resultSet) throws SQLException {
        Cat cat = mapCat(resultSet);
        bindParentsToCat(cat);
        bindChildrenToCat(cat);
        return cat;
    }

    private Cat mapCatById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return mapCat(resultSet);
        }
        return null;
    }

}
