package com.netcracker.cats.servlets;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;
import com.netcracker.cats.util.CatRequestUtil;
import com.netcracker.cats.util.InputFormValidation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class CatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();
    private final InputFormValidation validation = new InputFormValidation();
    private final CatRequestUtil catRequestUtil = new CatRequestUtil();

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
            if (cat == null) {
                resp.sendRedirect(req.getContextPath() + "/cats");
            } else {
                req.setAttribute("cat", cat);
                req.getRequestDispatcher("jsp/cat.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cat cat = catRequestUtil.createCatByRequest(req);
        String result = validation.checkCatForValid(cat);

        if (result.isEmpty()) {
            Cat newCat = catService.create(cat);
            resp.sendRedirect(req.getContextPath() + "/cats");
        } else {
            req.setAttribute("errorDescription", result);
            req.setAttribute("cat", cat);

//            catRequestUtil.addCatsAsListByGender(req);

            //ссылаться на url
            req.getRequestDispatcher("/create").forward(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cat cat = catRequestUtil.createCatByRequest(req);

        Long id = Long.valueOf(req.getParameter("id"));
        cat.setId(id);

        String result = validation.checkCatForValid(cat);

        if (result.isEmpty()) {
            Cat updatedCat = catService.update(cat);
            resp.sendRedirect(req.getContextPath() + "/cats");
        } else {
            req.setAttribute("errorDescription", result);
            req.setAttribute("cat", cat);

            catRequestUtil.addCatsAsListByGender(req);

            req.getRequestDispatcher("jsp/updateCat.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        System.out.println("Cat deleted " + catService.deleteById(id));
        resp.sendRedirect(req.getContextPath() + "/cats");
    }

}
