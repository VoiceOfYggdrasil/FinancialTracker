package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {

    private static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final LocalDateTime NOW = LocalDateTime.now();

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid option");
                    break;
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        String lines;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            while ((lines = bufferedReader.readLine()) != null) {

                String[] receipts = lines.split("\\|");

                //This formatting method makes way more sense to me than the last one (neighborhoodLibrary)
                transactions.add(new Transaction(LocalDate.parse(receipts[0]), LocalTime.parse(receipts[1]),
                        receipts[2], receipts[3], Double.parseDouble(receipts[4])));
            }
        } catch (IOException e) {
            System.err.println("ERROR while reading the file.");
        }
    }

    private static void addDeposit(Scanner scanner) {
        LocalDate date = null;
        LocalTime time = null;
        String description = null;
        String vendor = null;
        double amount = 0;

        try {
            System.out.print("\nEnter the date of the deposit (yyyy-MM-dd): ");
            date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            System.out.print("Enter the time of the deposit (HH:mm:ss): ");
            time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);

            System.out.print("Enter the description: ");
            description = scanner.nextLine().trim();

            System.out.print("Enter the vendor name: ");
            vendor = scanner.nextLine().trim();

            System.out.print("Enter the amount: ");
            amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) { // Making sure the value of the number input remains positive.
                System.out.println("\nERROR: Cannot enter value less than or equal to 0.");
                return;
            }

            Transaction transaction = new Transaction(date, time, description, vendor, amount);
            transactions.add(transaction);

            //Making sure I use 'true' so it actually saves the entry
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            bufferedWriter.write(transaction.toString());
            bufferedWriter.newLine();
            bufferedWriter.close();

            System.out.println("\nDeposit of $" + String.format("%.2f", amount) + " successfully processed.");

        } catch (Exception e) {
            System.err.println("\nERROR occurred while entering deposit.");
        }
    }

    private static void addPayment(Scanner scanner) {
        LocalDate date = null;
        LocalTime time = null;
        String description = null;
        String vendor = null;
        double amount = 0;

        try {
            System.out.print("\nEnter the date of the payment (yyyy-MM-dd): ");
            date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            System.out.print("Enter the time of the payment (HH:mm:ss): ");
            time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);

            System.out.print("Enter the description: ");
            description = scanner.nextLine().trim();

            System.out.print("Enter the vendor name: ");
            vendor = scanner.nextLine().trim();

            System.out.print("Enter the amount: ");
            amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("\nERROR: Cannot enter value less than or equal to 0.");
                return;
            }

            //Make the amount negative because it's a payment.
            double amountNegative = amount * -1;

            Transaction transaction = new Transaction(date, time, description, vendor, amountNegative);
            transactions.add(transaction);

            //True again, Mr. Bond.
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            bufferedWriter.write(transaction.toString());
            bufferedWriter.newLine();
            bufferedWriter.close();

            System.out.println("\nPayment of $" + String.format("%.2f", amount) + " successfully processed.");

        } catch (Exception e) {
            System.err.println("\nERROR occurred while entering payment.");
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\nLedger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;

                case "D":
                    displayDeposits();
                    break;

                case "P":
                    displayPayments();
                    break;

                case "R":
                    reportsMenu(scanner);
                    break;

                case "H":
                    running = false;

                default:
                    System.out.println("\nInvalid option");
                    break;
            }
        }
    }

    // This will display all transactions.
    private static void displayLedger() {
        System.out.println("\nHere's a table of all transactions:");
        System.out.println("\nDate | Time | Description | Vendor | Amount\n");

        //Just to keep things compact.
        for (Transaction transaction : transactions) {
            //Keeping toString to compare differences.
            //System.out.println(transaction.toString());
            System.out.println(transaction.getDate() + " | " + transaction.getTime() + " | "
            + transaction.getDescription() + " | " + transaction.getVendor() + " | " + transaction.getAmount());
        }
    }

    // This will display a list of all deposits.
    private static void displayDeposits() {
        System.out.println("\nHere's a list of all deposits:");
        System.out.println("\nDate | Time | Description | Vendor | Amount\n");

        for (Transaction transaction : transactions)
            if (transaction.getAmount() > 0) {
                //System.out.println(transaction.toString());
                System.out.println(transaction.getDate() + " | " + transaction.getTime() + " | "
                        + transaction.getDescription() + " | " + transaction.getVendor() + " | "
                        + transaction.getAmount());
            }
    }

    // This will display all payments.
    private static void displayPayments() {
        System.out.println("\nHere's a list of all payments:");
        System.out.println("\nDate | Time | Description | Vendor | Amount\n");

        for (Transaction transaction : transactions)
            if (transaction.getAmount() < 0) {
                //System.out.println(transaction.toString());
                System.out.println(transaction.getDate() + " | " + transaction.getTime() + " | "
                        + transaction.getDescription() + " | " + transaction.getVendor() + " | "
                        + transaction.getAmount());
            }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\nReports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": // For month-to-date transactions.
                    filterTransactionsByDate(NOW.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate(),
                            LocalDate.from(NOW));
                    break;

                case "2": // Transactions of the previous month.
                    filterTransactionsByDate(NOW.toLocalDate().minusMonths(1)
                            .minusDays(NOW.getDayOfMonth()), NOW.toLocalDate().minusMonths(1)
                            .with(TemporalAdjusters.lastDayOfMonth()));
                    break;

                case "3": // Transactions for the current year.
                    filterTransactionsByDate(NOW.toLocalDate().with(TemporalAdjusters.firstDayOfYear()),
                            NOW.toLocalDate());
                    break;

                case "4": // Transactions for previous year.
                    filterTransactionsByDate(NOW.with(TemporalAdjusters.firstDayOfYear())
                            .toLocalDate().minusYears(1),
                            NOW.with(TemporalAdjusters.firstDayOfYear()).toLocalDate());
                    break;

                case "5": // User inputs vendor name they want to isolate.
                    System.out.print("\nEnter the Vendor name you want to look for: ");
                    String vendorSearch = scanner.nextLine();
                    filterTransactionsByVendor(vendorSearch);
                    break;

                case "0":
                    running = false;

                default:
                    System.out.println("\nInvalid option");
                    break;
            }
        }
    }


    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        System.out.println("\nTransactions between " + startDate + " - " + endDate + ":");
        boolean found = false; // Defaulting to there being no transactions found.

        for (Transaction transaction : transactions) {
            if (transaction.getDate().isAfter(startDate) && transaction.getDate().isBefore(endDate)) {
                System.out.println(transaction.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Error: no entries.");
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        System.out.println("\nTransactions by \"" + vendor + "\" : ");
        boolean found = false;

        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(transaction.toString());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Error: no entries.");
        }
    }
}