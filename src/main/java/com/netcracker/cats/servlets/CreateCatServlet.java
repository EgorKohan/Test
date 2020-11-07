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
import java.util.stream.Collectors;

@WebServlet("/create")
public class CreateCatServlet extends HttpServlet {

    private final CatService catService = new CatServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Cat> males = catService.getAll().stream()
                .filter(cat -> cat.getGender().equals(Gender.MALE))
                .collect(Collectors.toList());
        List<Cat> females = catService.getAll().stream()
                .filter(cat -> cat.getGender().equals(Gender.FEMALE))
                .collect(Collectors.toList());

        req.setAttribute("males", males);
        req.setAttribute("females", females);

        req.getRequestDispatcher("jsp/createCat.jsp").forward(req, resp);
    }



}
