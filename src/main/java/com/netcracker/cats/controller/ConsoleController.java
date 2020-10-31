package com.netcracker.cats.controller;

import com.netcracker.cats.service.CatService;
import com.netcracker.cats.service.CatServiceImpl;
import com.netcracker.cats.util.ConsolePrinter;

import java.util.Scanner;

public class ConsoleController {

    private final CatService catService = new CatServiceImpl();
    private final ConsolePrinter consolePrinter = new ConsolePrinter();

    public void printMenuItem(){
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
                    consolePrinter.printAllCats(
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

}
