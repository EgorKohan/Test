package com.netcracker.cats.controller;

import com.netcracker.cats.model.Cat;
import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;

import java.util.List;
import java.util.Scanner;

public class ConsoleController {

    private final CatService catService = new CatServiceImpl();

    public void printMenuItem() {
        System.out.print("Select an action:" +
                "\n1 - Print info about all cats." +
                "\n2 - Add new cat." +
                "\n3 - Search a cat by id." +
                "\n4 - Exit." +
                "\nSelect: "
        );
    }

    public void start() {
        System.out.println("Hello");
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            printMenuItem();
            while (!scanner.hasNextInt()) {
                System.out.print("Incorrect input! Try again: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            switch (choice) {
                case 1: {
                    printAllCats(
                            catService.getAll()
                    );
                    break;
                }
                case 2: {
                    //TODO add catService.add method
                    break;
                }
                case 3: {
                    //TODO print info by ID
                    break;
                }
                case 4: {
                    break;          /*exit point*/
                }
                default: {
                    System.out.println("Wrong choice");
                    break;
                }
            }
        } while (choice != 4);
    }

    private void printAllCats(List<Cat> cats) {
        for (Cat cat : cats) {
            printCatInfo(cat);
        }
    }

    private void printCatInfo(Cat cat) {
        String catInfo = ">>Cat #" + cat.getId() + ". " +
                "Name " + cat.getName() + ".";
        System.out.println(catInfo);
    }

}
