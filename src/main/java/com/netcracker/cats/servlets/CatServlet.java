package com.netcracker.cats.servlets;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;
import com.netcracker.cats.util.MapCatByRequestUtil;
import com.netcracker.cats.util.InputFormValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();
    private final InputFormValidation validation = new InputFormValidation();
    private final MapCatByRequestUtil mapCatByRequestUtil = new MapCatByRequestUtil();
    private static final Logger LOGGER = LoggerFactory.getLogger(CatServlet.class);

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
        Cat cat = mapCatByRequestUtil.createCatByRequest(req);
        String result = validation.checkCatForValid(cat);

        if (result.isEmpty()) {
            Cat newCat = catService.create(cat);
            LOGGER.info("Create a new cat, id: {}", newCat.getId());
            resp.sendRedirect(req.getContextPath() + "/cats");
        } else {
            req.setAttribute("errorDescription", result);
            req.setAttribute("cat", cat);
            req.getRequestDispatcher("/create").forward(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cat cat = mapCatByRequestUtil.createCatByRequest(req);
        Long id = Long.valueOf(req.getParameter("id"));
        cat.setId(id);
        cat.getChildren().addAll(catService.getById(id).getChildren());

        String result = validation.checkCatForValid(cat);

        if (result.isEmpty()) {
            Cat updatedCat = catService.update(cat);
            LOGGER.info("Update a cat with id: {}", cat.getId());
            resp.sendRedirect(req.getContextPath() + "/cats");
        } else {
            req.setAttribute("errorDescription", result);
            req.setAttribute("cat", cat);
            req.getRequestDispatcher("/update").forward(req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = Long.valueOf(req.getParameter("id"));
        LOGGER.info("Cat deleted {}", catService.deleteById(id));
        resp.sendRedirect(req.getContextPath() + "/cats");
    }

}
