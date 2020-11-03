package com.netcracker.cats.service;

import com.netcracker.cats.dao.CatDao;
import com.netcracker.cats.dao.CatDaoJdbcImpl;
import com.netcracker.cats.model.Cat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatServiceImpl implements CatService {

    private final CatDao catDao = new CatDaoJdbcImpl();

    @Override
    public Cat getById(Long id) {
        try {
            return catDao.getById(id);
        } catch (SQLException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Cat> getByAge(int age) {
        if (age < 0 || age > 21) {
            System.out.println(">> Incorrect data");
            return null;
        }
        try {
            return catDao.getByAge(age);
        } catch (SQLException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Cat> getAll() {
        try {
            return catDao.getAll();
        } catch (SQLException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Cat create(Cat cat) {
        try {
            return catDao.create(cat);
        } catch (SQLException throwables) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Cat update(Cat cat) {
        try {
            return catDao.update(cat);
        } catch (SQLException throwables) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            return catDao.deleteById(id);
        } catch (SQLException throwables) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<Cat> findCatsByName(String name) {
        List<Cat> cats = getAll();
        List<Cat> filteredCats = new ArrayList<>();
        Pattern pattern = Pattern.compile(name);
        Matcher matcher;
        for (Cat cat : cats) {
            matcher = pattern.matcher(cat.getName());
            if (matcher.find()) {
                filteredCats.add(cat);
            }
        }
        return filteredCats;
    }

    @Override
    public String buildFamilyTree(Long id) {
        StringBuilder stringBuilder = new StringBuilder();
        Cat cat = getById(id);
        Cat father = cat.getFather();
        Cat mother = cat.getMother();

        stringBuilder.append("Fathers: ").append(cat.getName());
        while (father != null) {
            stringBuilder.append(" <- ").append(father.getName());
            father = father.getFather();
        }
        stringBuilder.append("\n");

        stringBuilder.append("Mothers: ").append(cat.getName());
        while (mother != null) {
            stringBuilder.append(" <- ").append(mother.getName());
            mother = mother.getMother();
        }
        return stringBuilder.toString();
    }
}
