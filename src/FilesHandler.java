import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FilesHandler {
    private final List<Path> filesList;
    private final SortingMode mode;
    private final DataType type;
    private final Path outFile;
    private int fileNumber;
    private int countLines = 0;
    private final int maxCharsInFile = 1000000;
    private final Path tmpFolder = Paths.get("tmp");
    private final List<Path> tmpFiles = new ArrayList<>();

    public FilesHandler(List<Path> filesList, SortingMode mode, DataType type, Path outFile) {
        this.filesList = filesList;
        this.mode = mode;
        this.type = type;
        this.outFile = outFile;
    }

    public void startProcessing() {
        try {
            fileNumber = 1;
            for (Path file : filesList) {
                if (!Files.exists(file)) {
                    System.out.println("Файл " + file + " не найден");
                    System.exit(0);
                } else {
                    switch (type) {
                        case STRING: {
                            preparingFileString(file);
                            break;
                        }
                        case INTEGER: {
                            preparingFileInteger(file);
                            break;
                        }
                    }
                }
            }
            if (countLines > 0) {
                mergeFiles();
                deleteTmpDir();
                System.out.println("Программа завершена. Отсортированные данные записаны в файл: " + outFile);
            } else {
                System.out.println("Нет элементов для слияния. Данные не записаны.");
            }
        } catch (OutOfMemoryError ex) {
            System.out.println("Размер входных данных превышает допустимый. Попробуйте запуск программы с меньшим объемом данных.");
            deleteTmpDir();
        }
    }

    private void preparingFileInteger(Path file) throws OutOfMemoryError {
        int countChars = 0;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            ArrayList<Integer> listInteger = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                if (countChars < maxCharsInFile) {
                    line = line.trim();
                    if (checkingSpaces(line)) {
                        try {
                            if (checkingRange(Long.parseLong(line))) {
                                int value = Integer.parseInt(line);
                                countChars += line.length();
                                listInteger.add(value);
                            } else {
                                System.out.println("Число '" + line + "' в файле '" + file +
                                        "' выходит за пределы допустимого диапазона класса Integer" +
                                        " и исключено из последовательности.");
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println("Элемент '" + line + "' в файле '" + file + "' не является целым числом." +
                                    " Элемент убран из последовательности.");
                        }
                    }
                    line = br.readLine();
                } else {
                    sortAndWrite(listInteger);
                    listInteger.clear();
                    countChars = 0;
                }
            }
            if (listInteger.size() > 0) {
                countLines += listInteger.size();
                sortAndWrite(listInteger);
            }
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла '" + file + "' : " + ex.getMessage());
        }
    }

    private void preparingFileString(Path file) throws OutOfMemoryError {
        int countChars = 0;
        try (BufferedReader br = Files.newBufferedReader(file)) {
            ArrayList<String> listString = new ArrayList<>();
            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if (countChars < maxCharsInFile) {
                    if (checkingSpaces(line)) {
                        listString.add(line);
                        countChars += line.length();
                    }
                    line = br.readLine();
                } else {
                    String[] stringArray = new String[listString.size()];
                    stringArray = listString.toArray(stringArray);
                    stringArray = new Sorter(mode).sort(stringArray);
                    writeTmpFile(stringArray);
                    listString.clear();
                    countChars = 0;
                }
            }
            if (listString.size() > 0) {
                countLines += listString.size();
                String[] stringArray = new String[listString.size()];
                stringArray = listString.toArray(stringArray);
                stringArray = new Sorter(mode).sort(stringArray);
                writeTmpFile(stringArray);
            }
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла '" + file + "' : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mergeFiles() throws OutOfMemoryError {
        if (tmpFiles.size() == 1) {
            writeOutFile(tmpFiles.get(0));
        } else {
            switch (type) {
                case INTEGER: {
                    int[] array1 = readFileInteger(tmpFiles.get(0));
                    int[] array2 = readFileInteger(tmpFiles.get(1));
                    int[] newArray = new int[array1.length + array2.length];
                    new Sorter(mode).mergeArraysInteger(array1, array2, newArray, mode);
                    try {
                        writeTmpFile(newArray);
                        removeTmpFile(tmpFiles.get(1));
                        removeTmpFile(tmpFiles.get(0));
                    } catch (IOException ex) {
                        System.out.println("Ошибка записи во временный файл : " + ex.getMessage());
                    }
                    mergeFiles();
                    break;
                }
                case STRING: {
                    String[] array1 = readFileString(tmpFiles.get(0));
                    String[] array2 = readFileString(tmpFiles.get(1));
                    String[] newArray = new String[array1.length + array2.length];
                    new Sorter(mode).mergeArraysString(array1, array2, newArray, mode);
                    try {
                        writeTmpFile(newArray);
                        removeTmpFile(tmpFiles.get(1));
                        removeTmpFile(tmpFiles.get(0));
                    } catch (IOException ex) {
                        System.out.println("Ошибка записи во временный файл : " + ex.getMessage());
                    }
                    mergeFiles();
                    break;
                }
            }
        }
    }

    private String[] readFileString(Path file) throws OutOfMemoryError {
        ArrayList<String> listString = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine();
            while (line != null) {
                listString.add(line);
                line = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла '" + file + "' : " + ex.getMessage());
            ex.printStackTrace();
        }
        return listString.toArray(String[]::new);
    }

    private int[] readFileInteger(Path file) throws OutOfMemoryError {
        ArrayList<Integer> listInteger = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line = br.readLine();
            while (line != null) {
                try {
                    int value = Integer.parseInt(line);
                    listInteger.add(value);
                } catch (NumberFormatException ex) {
                    System.out.println("Элемент '" + line + "' в файле '" + file + "' не является целым числом " +
                            "(в диапазоне от " + Integer.MIN_VALUE + " до " + Integer.MAX_VALUE +
                            "). Элемент удален из последовательности.");
                }
                line = br.readLine();
            }
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла '" + file + "' : " + ex.getMessage());
            ex.printStackTrace();
        }
        return listInteger.stream().mapToInt(Integer::valueOf).toArray();
    }

    private boolean checkingRange(long number) {
        return (number <= Integer.MAX_VALUE && number >= Integer.MIN_VALUE);
    }

    private boolean checkingSpaces(String string) {
        boolean result = true;
        if (string.matches(".*\\s+.*")) {
            System.out.println("Элемент '" + string + "' содержит пробел. " +
                    "Элемент удален из последовательности.");
            result = false;
        }
        if (string.equals("")) {
            System.out.println("Элемент '" + string + "' является пустой строкой. " +
                    "Элемент удален из последовательности.");
            result = false;
        }
        return result;
    }

    private void sortAndWrite(ArrayList<Integer> list) {
        int[] intArray = list.stream().mapToInt(Integer::valueOf).toArray();
        intArray = new Sorter(mode).sort(intArray);
        try {
            writeTmpFile(intArray);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writeTmpFile(String[] array) throws IOException {
        Path tmpFile = Paths.get(".\\tmp\\tmp_" + fileNumber + ".txt");
        if (!Files.exists(tmpFolder)) {
            Files.createDirectory(tmpFolder);
        }
        tmpFiles.add(tmpFile);
        fileNumber++;
        try (BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {
            for (int i = 0; i < array.length; i++) {
                writer.write(array[i]);
                if (i < array.length - 1) {
                    writer.newLine();
                }
            }
        }
    }

    private void writeTmpFile(int[] array) throws IOException {
        Path tmpFile = Paths.get(".\\tmp\\tmp_" + fileNumber + ".txt");
        if (!Files.exists(tmpFolder)) {
            Files.createDirectory(tmpFolder);
        }
        tmpFiles.add(tmpFile);
        fileNumber++;
        try (BufferedWriter writer = Files.newBufferedWriter(tmpFile)) {
            for (int i = 0; i < array.length; i++) {
                writer.write(String.valueOf(array[i]));
                if (i < array.length - 1) {
                    writer.newLine();
                }
            }
        }
    }

    public void removeTmpFile(Path file) {
        tmpFiles.remove(file);
        try {
            Files.delete(file);
        } catch (IOException ex) {
            System.out.println("Ошибка при удалении временного файла: " + ex.getMessage());
        }

    }

    public void deleteTmpDir() {
        try {
            File dir = tmpFolder.toFile();
            File[] fileArray = dir.listFiles();
            assert fileArray != null;
            for (File file : fileArray) {
                Files.delete(file.toPath());
            }
            Files.delete(tmpFolder);
        } catch (IOException ex) {
            System.out.println("Ошибка при удалении каталога с временными файлами");
        }
    }

    private void writeOutFile(Path file) {
        try {
            Files.copy(file, outFile, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
        } catch (IOException ex) {
            System.out.println("Ошибка при записи в файл : " + ex.getMessage());
        }
    }
}
