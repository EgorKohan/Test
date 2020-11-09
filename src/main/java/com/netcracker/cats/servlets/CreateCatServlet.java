package com.netcracker.cats.servlets;

import com.netcracker.cats.util.CatRequestUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/create")
public class CreateCatServlet extends HttpServlet {

    private final CatRequestUtil catRequestUtil = new CatRequestUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        catRequestUtil.addCatsAsListByGender(req);
        req.getRequestDispatcher("jsp/createCat.jsp").forward(req, resp);
    }

}
