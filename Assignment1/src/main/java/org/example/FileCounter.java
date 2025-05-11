package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class FileCounter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;



        do {
            int javaFileCount = 0, issueCount=0;
            System.out.print("\nEnter the folder path to scan: ");
            String folderPath = scanner.nextLine().trim();

            if (folderPath.startsWith("\"") && folderPath.endsWith("\"")) {
                folderPath = folderPath.substring(1, folderPath.length() - 1);
            }

            folderPath = folderPath.replace("\\", "/");
            File directory = new File(folderPath);

            if (!directory.isDirectory()) {
                System.out.println("Invalid directory: " + folderPath);
            } else {

                // Step 1: Prompt for thread count
                System.out.print("\nEnter the number of threads to use (More accurance using higher thread): ");
                int numThreads = Integer.parseInt(scanner.nextLine().trim());

                // Step 2: Collect all Java files
                List<File> javaFiles = new ArrayList<>();
                collectJavaFiles(directory, javaFiles);

                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                List<Future<Integer>> results = new ArrayList<>();

                // Step 3: Split files evenly for threads
                int chunkSize = (int) Math.ceil((double) javaFiles.size() / numThreads);
                for (int i = 0; i < javaFiles.size(); i += chunkSize) {
                    List<File> subList = javaFiles.subList(i, Math.min(i + chunkSize, javaFiles.size()));
                    results.add(executor.submit(new FileScanTask(subList)));
                }



                try {
                    for (Future<Integer> result : results) {
                        javaFileCount += chunkSize;
                        issueCount += result.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Error during multithreaded execution.");
                }

                executor.shutdown();


                // Step 5: Display results
                System.out.println("\nNumber of Java Files = " + javaFileCount);
                System.out.println("Number of Issues (with // SOLVED) = " + issueCount);

                // Step 6: List the Java files
                System.out.println("\nDetected Java files:");
                for (File file : javaFiles) {
                    System.out.println("- " + file.getAbsolutePath());
                }

            }



            System.out.print("\nPress 1 to exit, or any other key to scan another folder: ");
            String input = scanner.nextLine();
            choice = input.equals("1") ? 1 : 0;
            System.out.println("---------------------------------------------------------------");

        } while (choice != 1);

        System.out.println("Exiting program. Goodbye!");
    }

    // Recursively collect all .java files
    private static void collectJavaFiles(File file, List<File> javaFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    collectJavaFiles(subFile, javaFiles);
                }
            }
        } else if (file.isFile() && file.getName().endsWith(".java")) {
            javaFiles.add(file);
        }
    }

    // Thread task to count issues in a sublist of Java files
    static class FileScanTask implements Callable<Integer> {
        private final List<File> files;

        public FileScanTask(List<File> files) {
            this.files = files;
        }

        @Override
        public Integer call() {
            int issueCount = 0;
            for (File file : files) {
                try {
                    boolean hasIssue = Files.lines(file.toPath())
                            .anyMatch(line -> line.contains("// SOLVED"));
                    if (hasIssue) {
                        issueCount++;
                    }
                } catch (IOException e) {
                    System.out.println("Failed to read file: " + file.getAbsolutePath());
                }
            }
            return issueCount;
        }
    }
}
