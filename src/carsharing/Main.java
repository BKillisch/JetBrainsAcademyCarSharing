package carsharing;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    private static DBHandler dbHandler;
    private static boolean exit;
    private static Scanner scanner;

    public static void main(String[] args) {
        String dbFileName = "carSharingDB";
        if (args.length == 2 && "-databaseFileName".equals(args[0])) {
            dbFileName = args[1];
        }

        File dbDir = new File("../task/carsharing/db/");
        dbDir.mkdirs();

        dbHandler = new DBHandler(dbFileName);

        exit = false;
        scanner = new Scanner(System.in);

        while (!exit) {
            System.out.println();
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");
            switch (checkUserInput("[0123]", scanner.nextLine())) {
                case 1:
                    loginAsManager();
                    break;
                case 2:
                    int id = printList("customer", true, 0);
                    if (id != -1) {
                        customerMenu((Customer) dbHandler.get(id, "customer"));
                    }
                    break;
                case 3:
                    createCustomer();
                    break;
                case 0:
                    exit = true;
                    break;
            }
        }
    }

    private static void loginAsManager() {
        System.out.println();
        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back");
        switch (checkUserInput("[012]", scanner.nextLine())) {
            case 1:
                int id = printList("company", true, 0);
                if (id != -1) {
                    System.out.println("'" + dbHandler.get(id, "company").getName() + "' company");
                    companyMenu(id);
                }

                loginAsManager();
                break;
            case 2:
                createCompany();
                loginAsManager();
                break;
            case 0:
                break;
        }
    }

    private static int printList(String className, boolean choice, int compId) {
        var items = dbHandler.getAll(className);
        if (className.equals("car")) {
            items = items.stream()
                    .filter(dbObject -> ((Car) dbObject).getCompany_id() == compId)
                    .collect(Collectors.toList());
        }

        System.out.println();

        if (items.size() == 0) {
            System.out.println("The " + className + " list is empty!");
        } else {
            if (choice) {
                System.out.println("Choose a " + className + ":");
            } else {
                System.out.println(className + " list:");
            }
            int i = 1;
            for (var e: items) {
                System.out.println(i + ". " + e.getName());
                i++;
            }

            if (choice) {
                System.out.println("0. Back");

                int id = checkUserInput("\\d*", scanner.nextLine()) - 1;

                if (id == -1) {
                    return -1;
                } else {
                    return items.get(id).getId();
                }
            }
        }
        return -1;
    }

    private static void createCompany() {
        System.out.println();
        System.out.println("Enter the company name:");

        dbHandler.add(new Company(scanner.nextLine()));

        System.out.println("The company was created!");
    }

    private static void companyMenu(int id) {
        System.out.println();
        System.out.println("1. Car list");
        System.out.println("2. Create a car");
        System.out.println("0. Back");

        switch (checkUserInput("[012]", scanner.nextLine())) {
            case 1:
                printList("car", false, id);
                companyMenu(id);
                break;
            case 2:
                createCar(id);
                companyMenu(id);
                break;
            case 0:
                break;
        }
    }

    private static int printAvailableCarList(Company company) {
        var items = dbHandler.getAvailableCarsAtComp(company.getId());

        System.out.println();

        if (items.size() == 0) {
            System.out.println("No available cars in the " + company.getName() + " company");
        } else {
            System.out.println("Choose a car:");

            int i = 1;
            for (var e: items) {
                System.out.println(i + ". " + e.getName());
                i++;
            }

            System.out.println("0. Back");

            int id = checkUserInput("\\d*", scanner.nextLine()) - 1;

            if (id == -1) {
                return -1;
            } else {
                System.out.println("You rented '" + items.get(id).getName() + "'");
                return items.get(id).getId();
            }
        }
        return -1;
    }

    private static void createCar(int id) {
        System.out.println();
        System.out.println("Enter the car name:");

        dbHandler.add(new Car(scanner.nextLine(), id));

        System.out.println("The car was created!");
    }

    private static void createCustomer() {
        System.out.println();
        System.out.println("Enter the customer name:");

        dbHandler.add(new Customer(scanner.nextLine()));

        System.out.println("The customer was created!");
    }

    private static void customerMenu(Customer customer) {
        System.out.println();
        System.out.println("1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");

        switch (checkUserInput("[0123]", scanner.nextLine())) {
            case 1:
                if (customer.getRented_car_id() != -1) {
                    System.out.println();
                    System.out.println("You've already rented a car!");
                } else {
                    int id = printList("company", true, 0);
                    if (id != -1) {
                        Company company = (Company) dbHandler.get(id, "company");
                        id = printAvailableCarList(company);

                        if (id != -1) {
                            dbHandler.updateUserRentStatus(id, customer.getId());
                            customer.setRented_car_id(id);
                        }
                    }
                }
                customerMenu(customer);
                break;
            case 2:
                if (customer.getRented_car_id() != -1) {
                    System.out.println("You've returned a rented car!");
                    dbHandler.updateUserRentStatus(-1, customer.getRented_car_id());
                } else {
                    System.out.println("You didn't rent a car!");
                }
                customerMenu(customer);
                break;
            case 3:
                if (customer.getRented_car_id() != -1) {
                    Car rentedCar = (Car) dbHandler.get(customer.getRented_car_id(), "car");
                    System.out.println("Your rented car:");
                    System.out.println(rentedCar.getName());
                    System.out.println("Company:");
                    Company company = (Company) dbHandler.getAll("company").stream()
                            .filter(dbObject -> dbObject.getId() == rentedCar.getCompany_id())
                            .findFirst().get();
                    System.out.println(company.getName());
                } else {
                    System.out.println("You didn't rent a car!");
                }
                customerMenu(customer);
                break;
            case 0:
                break;
        }

    }

    /**
     * checks if a String consist of number specified in a regular expression and
     * returns the number as an integer. If the String does not match the regex or can not be converted
     * to integer, -1 will be returned. This is used to check if the user has entered one of the possible
     * numbers the perform an action in a menu, or to check card numbers, pins and amounts of money entered by the user.
     *
     * @param regex the regular expression the string must match
     * @param userInput the String that will be checked
     * @return the integer value of the String or -1
     */
    private static int checkUserInput(String regex, String userInput) {
        try {
            if (userInput.matches(regex)) {
                return Integer.parseInt(userInput);
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }
}