import java.io.*;
import java.util.*;

class Record {
    private String id;
    private String name;
    private String email;

    public Record(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-30s", id, name, email);
    }

    public static Record fromString(String recordLine) {
        String[] parts = recordLine.split("\\|");
        return new Record(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }
}

public class CRUDOperations {
    private static final String FILE_NAME = "records.txt";
    private static final String HEADER = "ID         | Name                 | Email                       \n"
            + "-------------------------------------------------------------";

    // Ensure file exists with a header
    private void ensureFileExistsWithHeader() throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(HEADER);
                writer.newLine();
            }
        }
    }

    // Append record to file
    public void createRecord(Record record) throws IOException {
        ensureFileExistsWithHeader();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(record.toString());
            writer.newLine();
        }
    }

    // Read all records (skip header and separators)
    public List<Record> readAllRecords() throws IOException {
        ensureFileExistsWithHeader();
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean skipHeader = true;
            while ((line = reader.readLine()) != null) {
                if (skipHeader || line.startsWith("-")) {
                    skipHeader = false;
                    continue;
                }
                records.add(Record.fromString(line));
            }
        }
        return records;
    }

    // Update record by ID
    public boolean updateRecord(String id, Record updatedRecord) throws IOException {
        ensureFileExistsWithHeader();
        List<Record> records = readAllRecords();
        boolean isUpdated = false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(HEADER);
            writer.newLine();
            for (Record record : records) {
                if (record.getId().equals(id)) {
                    writer.write(updatedRecord.toString());
                    isUpdated = true;
                } else {
                    writer.write(record.toString());
                }
                writer.newLine();
            }
        }
        return isUpdated;
    }

    // Delete record by ID
    public boolean deleteRecord(String id) throws IOException {
        ensureFileExistsWithHeader();
        List<Record> records = readAllRecords();
        boolean isDeleted = false;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write(HEADER);
            writer.newLine();
            for (Record record : records) {
                if (record.getId().equals(id)) {
                    isDeleted = true;
                } else {
                    writer.write(record.toString());
                    writer.newLine();
                }
            }
        }
        return isDeleted;
    }

    // Display records (same format as file)
    public void displayRecords() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    public static void main(String[] args) {
        CRUDOperations crud = new CRUDOperations();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nCRUD Operations Menu:");
                System.out.println("1. Create Record");
                System.out.println("2. Read All Records");
                System.out.println("3. Update Record");
                System.out.println("4. Delete Record");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: // Create
                        System.out.print("Enter ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String email = scanner.nextLine();
                        crud.createRecord(new Record(id, name, email));
                        System.out.println("Record created successfully.");
                        break;

                    case 2: // Read
                        crud.displayRecords();
                        break;

                    case 3: // Update
                        System.out.print("Enter ID of the record to update: ");
                        String updateId = scanner.nextLine();
                        System.out.print("Enter new Name: ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new Email: ");
                        String newEmail = scanner.nextLine();
                        if (crud.updateRecord(updateId, new Record(updateId, newName, newEmail))) {
                            System.out.println("Record updated successfully.");
                        } else {
                            System.out.println("Record not found.");
                        }
                        break;

                    case 4: // Delete
                        System.out.print("Enter ID of the record to delete: ");
                        String deleteId = scanner.nextLine();
                        if (crud.deleteRecord(deleteId)) {
                            System.out.println("Record deleted successfully.");
                        } else {
                            System.out.println("Record not found.");
                        }
                        break;

                    case 5: // Exit
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        }
    }
}
