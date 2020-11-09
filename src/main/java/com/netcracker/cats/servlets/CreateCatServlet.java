package com.netcracker.cats.servlets;

import com.netcracker.cats.util.MapCatByRequestUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/create")
public class CreateCatServlet extends HttpServlet {

    private final MapCatByRequestUtil mapCatByRequestUtil = new MapCatByRequestUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mapCatByRequestUtil.addCatsAsListByGender(req);
        req.getRequestDispatcher("jsp/createCat.jsp").forward(req, resp);
    }

}
