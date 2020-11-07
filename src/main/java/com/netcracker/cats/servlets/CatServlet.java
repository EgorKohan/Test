package com.netcracker.cats.servlets;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.model.Gender;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//@WebServlet("/cats")
public class CatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) {
            List<Cat> cats = catService.getAll();
            req.setAttribute("cats", cats);
            req.getRequestDispatcher("jsp/cats.jsp").forward(req, resp);
        } else {
            Long catId = Long.valueOf(id);
            Cat cat = catService.getById(catId);
            req.setAttribute("cat", cat);
            req.getRequestDispatcher("jsp/cat.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String color = req.getParameter("color");
        int age = Integer.parseInt(req.getParameter("age"));
        Gender gender = Gender.valueOf(req.getParameter("gender").toUpperCase());
        Long fatherId = Long.valueOf(req.getParameter("father"));
        Long motherId = Long.valueOf(req.getParameter("mother"));
        Cat father = catService.getById(fatherId);
        Cat mother = catService.getById(motherId);

        Cat cat = Cat.builder()
                .name(name)
                .age(age)
                .gender(gender)
                .color(color)
                .father(father)
                .mother(mother)
                .build();

        Cat newCat = catService.create(cat);
        req.setAttribute("cat", newCat);
        resp.sendRedirect(req.getContextPath() + "/cats");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        String name = req.getParameter("name");
        String color = req.getParameter("color");
        int age = Integer.parseInt(req.getParameter("age"));

        Cat cat = catService.getById(id);
        cat.setName(name);
        cat.setColor(color);
        cat.setAge(age);

        Cat newCat = catService.update(cat);
        req.setAttribute("cat", newCat);
        resp.sendRedirect(req.getContextPath() + "/cats");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        System.out.println("Cat deleted " + catService.deleteById(id));
        resp.sendRedirect(req.getContextPath() + "/cats");
    }

}
