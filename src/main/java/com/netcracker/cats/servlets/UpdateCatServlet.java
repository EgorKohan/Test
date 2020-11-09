package com.netcracker.cats.servlets;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;
import com.netcracker.cats.util.CatRequestUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/update")
public class UpdateCatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();
    private final CatRequestUtil catRequestUtil = new CatRequestUtil();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        Cat cat = catService.getById(id);
        req.setAttribute("cat", cat);
        catRequestUtil.addCatsAsListByGender(req);
        req.getRequestDispatcher("jsp/updateCat.jsp").forward(req, resp);
    }

}
