//Ahmed Adel Ahmed Oraby
//OS  CLI.
//................................................//
import java.io.*;
import java.util.*;

class Parser {
    private String commandName;
    private String[] args;

    //takes a string input and parses it into a command name and an array of arguments.
    public boolean parse(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 0) {
            return false;
        }

        commandName = parts[0];
        args = Arrays.copyOfRange(parts, 1, parts.length);
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}
class Terminal {
    private final Parser parser;
    private String currentDirectory;

    public Terminal() {
        parser = new Parser();
        currentDirectory = "C:\\Users\\Asus\\IdeaProjects\\OS2"; // Initial directory
    }
    //C:\Users\Asus\IdeaProjects\os
    public void echo(String[] args)
    {//prints the concatenated form of the elements in the args array.
        System.out.println(String.join(" ", args));
        //joins the elements in the args array into a single string with each element separated by a space (" ")
    }

    public void pwd() {
        System.out.println(currentDirectory);
    }

    public void cd(String[] args) {
        //checks if the args array is empty or if the first argument is the tilde character (~).
        // If either condition is met, it sets the currentDirectory to the home directory.
        if (args.length == 0 || args[0].equals("~")) {
            currentDirectory = "/home/user"; // Change to home directory
        } else if (args[0].equals("..")) {
            // Go up one directory
            currentDirectory = new File(currentDirectory).getParent();
        } else {
            // Handling Absolute and Relative Paths:
            File newDir = new File(args[0]);
            if (newDir.isAbsolute()) {
                //sets the currentDirectory to that absolute path.
                currentDirectory = newDir.getAbsolutePath();
            } else {
                //constructs the new directory path based on the current directory and the provided relative path.
                currentDirectory = new File(currentDirectory, args[0]).getAbsolutePath();
            }
        }
    }

    public void ls() {
        File dir = new File(currentDirectory);
        //retrieves an array of File objects, which represent the files and directories contained in the dir.
        File[] files = dir.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    public void ls_r() {
        File dir = new File(currentDirectory);
        File[] files = dir.listFiles();
        if (files != null) {
            Arrays.sort(files, Collections.reverseOrder());
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    public void mkdir(String[] args) {
        for (String arg : args) {
            File dir = new File(arg);
            if (!dir.isAbsolute()) {
                dir = new File(currentDirectory, arg);
            }
            boolean created = dir.mkdir();
            if (created) {
                System.out.println("Directory created: " + dir.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + dir.getAbsolutePath());
            }
        }
    }

    public void rmdir(String[] args) {
        if (args.length == 1 && args[0].equals("*")) {
            File dir = new File(currentDirectory);
            File[] subDirs = dir.listFiles();
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    if (subDir.isDirectory() && subDir.list().length == 0) {
                        boolean deleted = subDir.delete();
                        if (deleted) {
                            System.out.println("Directory deleted: " + subDir.getAbsolutePath());
                        } else {
                            System.err.println("Failed to delete directory: " + subDir.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            for (String arg : args) {
                File dir = new File(arg);
                if (!dir.isAbsolute()) {
                    dir = new File(currentDirectory, arg);
                }
                if (dir.isDirectory() && dir.list().length == 0) {
                    boolean deleted = dir.delete();
                    if (deleted) {
                        System.out.println("Directory deleted: " + dir.getAbsolutePath());
                    } else {
                        System.err.println("Failed to delete directory: " + dir.getAbsolutePath());
                    }
                }
            }
        }
    }



    public void touch(String[] args) {
        for (String arg : args) {
            File file = new File(arg);
            if (!file.isAbsolute()) {
                file = new File(currentDirectory, arg);
            }

            // Ensure the parent directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    System.err.println("Error creating directory: " + parentDir.getAbsolutePath());
                    continue; // Skip file creation if directory creation failed
                }
            }

            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getAbsolutePath());
                } else {
                    System.err.println("File already exists: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
    }

    public void cp(String[] args) {
        if (args.length != 2) {
            System.err.println("cp command requires exactly two file arguments.");
            return;
        }

        File sourceFile = new File(args[0]);
        File targetFile = new File(args[1]);

        if (!sourceFile.isAbsolute()) {
            sourceFile = new File(currentDirectory, args[0]);
        }

        if (!targetFile.isAbsolute()) {
            targetFile = new File(currentDirectory, args[1]);
        }

        if (sourceFile.exists() && sourceFile.isFile()) {
            try (InputStream inStream = new FileInputStream(sourceFile);
                 OutputStream outStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
                System.out.println("File copied successfully: " + sourceFile.getName() + " to " + targetFile.getName());
            } catch (IOException e) {
                System.err.println("Error copying file: " + e.getMessage());
            }
        } else {
            System.err.println("Source file does not exist or is not a regular file.");
        }
    }


    public void cp_r(String[] args) {
        if (args.length != 2) {
            System.err.println("cp -r command requires two directory arguments.");
            return;
        }

        String sourceDirectory = args[0];
        String destinationDirectory = args[1];

        File sourceDir = new File(sourceDirectory);
        File destinationDir = new File(destinationDirectory);

        if (!sourceDir.isAbsolute()) {
            sourceDir = new File(currentDirectory, sourceDirectory);
        }

        if (!destinationDir.isAbsolute()) {
            destinationDir = new File(currentDirectory, destinationDirectory);
        }

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.err.println("Source directory does not exist or is not a directory.");
            return;
        }

        if (!destinationDir.exists()) {
            destinationDir.mkdir();
        } else if (!destinationDir.isDirectory()) {
            System.err.println("Destination is not a directory.");
            return;
        }

        copyDirectory(sourceDir, destinationDir);
        System.out.println("Directory copied successfully: " + sourceDir.getPath() + " to " + destinationDir.getPath());
    }

    private void copyDirectory(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyDirectory(srcFile, destFile);
            }
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                System.err.println("Error copying directory: " + e.getMessage());
            }
        }
    }


    public void rm(String[] args) {
        if (args.length != 1) {
            System.err.println("rm command requires one argument: file to remove");
            return;
        }

        File file = new File(args[0]);

        if (!file.isAbsolute()) {
            file = new File(currentDirectory, args[0]);
        }

        if (!file.exists() || !file.isFile()) {
            System.err.println("File does not exist or is not a regular file.");
            return;
        }

        if (file.delete()) {
            System.out.println("File removed.");
        } else {
            System.err.println("Error removing file.");
        }
    }



    public void cat(String[] args) {
        if (args.length == 1) {
            // Print the content of the specified file
            File file = new File(args[0]);
            if (!file.isAbsolute()) {
                file = new File(currentDirectory, args[0]);
            }

            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {  //reads the file line by line using the
                        System.out.println(line);            //BufferedReader and prints each line to the standard output.
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                }
            } else {
                System.err.println("File does not exist or is not a regular file.");
            }
        } else if (args.length == 2) {
            // Concatenate the content of two files and print
            File file1 = new File(args[0]);
            File file2 = new File(args[1]);

            if (!file1.isAbsolute()) {
                file1 = new File(currentDirectory, args[0]);
            }

            if (!file2.isAbsolute()) {
                file2 = new File(currentDirectory, args[1]);
            }

            if (file1.exists() && file2.exists() && file1.isFile() && file2.isFile()) {
                try (BufferedReader reader1 = new BufferedReader(new FileReader(file1));
                     BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {
                    String line;
                    while ((line = reader1.readLine()) != null) {
                        System.out.println(line);
                    }
                    while ((line = reader2.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading files: " + e.getMessage());
                }
            } else {
                System.err.println("Both files must exist and be regular files.");
            }
        } else {
            System.err.println("cat command requires one or two arguments.");
        }
    }


    public void wc(String[] args) {
        if (args.length != 1) {
            System.err.println("wc command requires one argument: file to count.");
            return;
        }

        File file = new File(args[0]);

        if (!file.isAbsolute()) {
            file = new File(currentDirectory, args[0]);
        }

        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                int lineCount = 0;
                int wordCount = 0;
                int charCount = 0;
                String line;
                while ((line = reader.readLine()) != null) {  // reads the content of the file line by line using the readLine
                    lineCount++;
                    String[] words = line.split("\\s+"); // splits the line into words,This creates an array of words from the line
                    wordCount += words.length;
                    charCount += line.length();
                }
                System.out.println(lineCount + " " + wordCount + " " + charCount + " " + file.getName());
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            System.err.println("File does not exist or is not a regular file.");
        }
    }


    public void history(List<String> commandHistory) {
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + " " + commandHistory.get(i));
        }
    }

    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        List<String> commandHistory = new ArrayList<>();

        System.out.println("Welcome to your custom CLI. Type 'exit' to quit.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(terminal.currentDirectory + "> ");
            String input = scanner.nextLine();
            commandHistory.add(input);

            if (input.equals("exit")) {
                break;
            }

            if (terminal.parser.parse(input)) {
                String commandName = terminal.parser.getCommandName();
                String[] commandArgs = terminal.parser.getArgs();

                switch (commandName) {
                    case "history" -> terminal.history(commandHistory);
                    default -> {
                        switch (commandName) {
                            case "echo" -> terminal.echo(commandArgs);
                            case "pwd" -> terminal.pwd();
                            case "cd" -> terminal.cd(commandArgs);
                            case "ls" -> terminal.ls();
                            case "ls_r" -> terminal.ls_r();
                            case "mkdir" -> terminal.mkdir(commandArgs);
                            case "rmdir" -> terminal.rmdir(commandArgs);
                            case "touch" -> terminal.touch(commandArgs);
                            case "cp" -> terminal.cp(commandArgs);
                            case "cp_r" -> terminal.cp_r(commandArgs);
                            case "rm" -> terminal.rm(commandArgs);
                            case "cat" -> terminal.cat(commandArgs);
                            case "wc" -> terminal.wc(commandArgs);
                            default -> System.err.println("Command not found. Type 'help' for available commands.");
                        }
                    }
                }
            } else {
                System.err.println("Invalid input. Please try again.");
            }
        }
    }
}
