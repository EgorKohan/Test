package com.netcracker.cats.service;

import com.netcracker.cats.dao.CatDao;
import com.netcracker.cats.dao.CatDaoJdbcImpl;
import com.netcracker.cats.model.Cat;

import java.sql.SQLException;
import java.util.List;

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
}
