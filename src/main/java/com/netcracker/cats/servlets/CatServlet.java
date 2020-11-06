package com.netcracker.cats.servlets;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/cats")
public class CatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Cat> cats = catService.getAll();
        req.setAttribute("cats", cats);
        req.getRequestDispatcher("jsp/cats.jsp").forward(req, resp);
    }

}
