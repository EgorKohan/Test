package com.netcracker.cats.dao;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.util.JdbcConnectionUtil;

import java.sql.*;
import java.util.*;

public class CatDaoJdbcImpl implements CatDao {

    //FIXME remove cache
    private final Map<Long, Cat> cache = new HashMap<>();
    private final Connection CONNECTION = new JdbcConnectionUtil().getConnection();

    //language=SQL
    private static final String SQL_SELECT_CATS_WITH_CHILDREN =
            "SELECT * FROM netcracker.cats " +
                    "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "ORDER BY child_id IS NULL desc ";
    //language=SQL
    private static final String SQL_FIND_CAT_BY_ID =
            "SELECT * FROM netcracker.cats " +
                    " LEFT JOIN netcracker.parents ON cats.id = parents.child_id" +
                    " WHERE id = ?";
    //language=SQL
    private static final String SQL_SELECT_CATS_BY_AGE =
            "SELECT * FROM netcracker.cats" +
                    " LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "WHERE age = ?";
    //language=SQL
    private static final String SQL_DELETE_CAT_BY_ID =
            "DELETE FROM netcracker.cats WHERE id = ?";
    //language=SQL
    private static final String SQL_UPDATE_CAT_BY_ID =
            "UPDATE netcracker.cats SET name = ?, gender = ?, color = ?, age = ? WHERE id = ?";
    //language=SQL
    private static final String SQL_INSERT_CAT_INTO_CATS =
            "INSERT INTO netcracker.cats(name, gender, color, age) " +
                    "VALUE(?, ?, ?, ?)";
    //language=SQL
    private static final String SQL_INSERT_CAT_INTO_PARENTS =
            "INSERT INTO netcracker.parents(child_id, father_id, mother_id) VALUE (?, ?, ?)";
    //language=SQL
    private static final String SQL_GET_LAST_CAT =
            "SELECT * FROM netcracker.cats " +
                    "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "ORDER BY id DESC LIMIT 1";
    //language=SQL
    private static final String SQL_GET_CHILDREN_BY_PARENT_ID =
            "SELECT * FROM netcracker.cats " +
                    "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "WHERE mother_id = ? OR  father_id = ?";
    //language=SQL
    private static final String SQL_FILTER_CATS_BY_PATTERN =
            "SELECT * FROM netcracker.cats " +
                    "LEFT JOIN netcracker.parents ON cats.id = parents.child_id " +
                    "WHERE cats.name LIKE ?";

    @Override
    public Cat getById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FIND_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        final ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return mapCatWithParents(resultSet);
        }
        return null;
    }

    @Override
    public List<Cat> getByAge(int age) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_SELECT_CATS_BY_AGE);
        preparedStatement.setInt(1, age);
        final ResultSet resultSet = preparedStatement.executeQuery();
        List<Cat> filteredCats = new ArrayList<>();
        while (resultSet.next()) {
            filteredCats.add(
                    mapCatWithParents(resultSet)
            );
        }
        return filteredCats;
    }

    @Override
    public List<Cat> getAll() throws SQLException {
        final ResultSet resultSet = CONNECTION.createStatement().executeQuery(SQL_SELECT_CATS_WITH_CHILDREN);
        List<Cat> cats = new ArrayList<>();

        while (resultSet.next()) {
            cats.add(
                    mapCatWithParents(resultSet)
            );
        }

        return cats;
    }

    @Override
    public Cat create(Cat cat) throws SQLException {
        PreparedStatement insertIntoCat = CONNECTION.prepareStatement(SQL_INSERT_CAT_INTO_CATS);
        insertIntoCat.setString(1, cat.getName());
        insertIntoCat.setString(2, cat.getGender().toString());
        insertIntoCat.setString(3, cat.getColor());
        insertIntoCat.setInt(4, cat.getAge());

        Cat father = cat.getFather();
        Cat mother = cat.getMother();


        if (!insertIntoCat.execute()) {
            cat = getLastCat();
        }

        if (cat != null) {
            cat.setFather(father);
            cat.setMother(mother);
        }

        if (cat != null) {
            PreparedStatement insertIntoParents = CONNECTION.prepareStatement(SQL_INSERT_CAT_INTO_PARENTS);
            insertIntoParents.setLong(1, 0);
            insertIntoParents.setLong(2, 0);
            insertIntoParents.setLong(3, 0);
            if (father != null) {
                insertIntoParents.setLong(1, cat.getId());
                insertIntoParents.setLong(2, father.getId());
            }
            if (mother != null) {
                insertIntoParents.setLong(1, cat.getId());
                insertIntoParents.setLong(3, mother.getId());
            }
            if ((mother != null || father != null)) {
                insertIntoParents.execute();
            }
        }

        return cat;
    }

    @Override
    public Cat update(Cat cat) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_UPDATE_CAT_BY_ID);
        preparedStatement.setString(1, cat.getName());
        preparedStatement.setInt(2, cat.getAge());
        preparedStatement.setString(3, cat.getColor());
        preparedStatement.setString(4, cat.getGender().toString());
        preparedStatement.setLong(5, cat.getId());

        if (preparedStatement.execute()) {
            PreparedStatement updateCatStatement = CONNECTION.prepareStatement(SQL_FIND_CAT_BY_ID);
            updateCatStatement.setLong(1, cat.getId());
            final ResultSet resultSet = updateCatStatement.executeQuery();

            if (resultSet.next()) {
                cache.remove(cat.getId());
                return mapCatWithParents(resultSet);
            }

        }

        return null;
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_DELETE_CAT_BY_ID);
        preparedStatement.setLong(1, id);
        return preparedStatement.execute();
    }

    private Cat mapCat(ResultSet resultSet) throws SQLException {
        Cat cat = null;
        Long id = resultSet.getLong("id");
        if (cache.containsKey(id)) {
            cat = cache.get(id);
            return cat;
        }

        cat = Cat.builder()
                .id(id)
                .name(resultSet.getString("name"))
                .age(resultSet.getInt("age"))
                .color(resultSet.getString("color"))
                .gender(Gender.valueOf(resultSet.getString("gender")))
                .build();

        cache.put(id, cat);

        return cat;
    }

    private Cat mapCatWithParents(ResultSet resultSet) throws SQLException {
        Cat cat = mapCat(resultSet);

        if (resultSet.getLong("child_id") != 0) {
            Long fatherId = resultSet.getLong("father_id");
            if (fatherId != 0) {
                Cat father;
                if (cache.containsKey(fatherId)) {
                    father = cache.get(fatherId);
                } else {
                    father = getById(fatherId);
                }
                cat.setFather(father);
                father.getChildren().add(cat);
            }

            Long motherId = resultSet.getLong("mother_id");
            if (motherId != 0) {
                Cat mother;
                if (cache.containsKey(motherId)) {
                    mother = cache.get(motherId);
                } else {
                    mother = getById(motherId);
                }
                cat.setMother(mother);
                mother.getChildren().add(cat);
            }
        }

        cat.getChildren().addAll(findChildrenForCat(cat));

        return cat;

    }

    private Set<Cat> findChildrenForCat(Cat cat) throws SQLException {
        Set<Cat> cats = new HashSet<>();
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_GET_CHILDREN_BY_PARENT_ID);
        preparedStatement.setLong(1, cat.getId());
        preparedStatement.setLong(2, cat.getId());
        final ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            cats.add(mapCatWithParents(resultSet));
        }
        return cats;
    }

    private Cat getLastCat() throws SQLException {
        final ResultSet resultSet = CONNECTION.createStatement().executeQuery(SQL_GET_LAST_CAT);

        if (resultSet.next()) {
            return mapCatWithParents(resultSet);
        }

        return null;
    }

    @Override
    public List<Cat> findCatsByName(String name) throws SQLException {
        PreparedStatement preparedStatement = CONNECTION.prepareStatement(SQL_FILTER_CATS_BY_PATTERN);
        String pattern = "%" + name + "%";
        preparedStatement.setString(1, pattern);
        final ResultSet resultSet = preparedStatement.executeQuery();
        List<Cat> filteredCats = new ArrayList<>();
        while (resultSet.next()) {
            filteredCats.add(
                    mapCatWithParents(resultSet)
            );
        }
        return filteredCats;
    }

    //create a method to map cat id with searching if there is not use in another cat

}
