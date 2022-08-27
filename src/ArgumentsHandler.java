import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArgumentsHandler {
    private SortingMode mode = SortingMode.ASC;
    private DataType type;
    private Path outputFile;
    private final List<Path> inputFileList = new ArrayList<>();

    public void checkingParameters(String[] args) {
        int index = 0;
        boolean typeSet = false;
        boolean modeSet = false;

        String INFO = "\n\n**** Параметры программы ****\n" +
                "> Режим сортировки (необязательный) : '-a' - по возрастанию (умолч.), '-d' - по убыванию\n" +
                "> Тип данных (обязательный) : '-s' - строки, '-i' - целые числа\n" +
                "> Имя выходного файла (обязательное)\n" +
                "> Имена выходных файлов (не менее одного, указываются через пробел)\n\n" +
                "Пример команды: mergesorting.exe -a -i out.txt in1.txt in2.txt in3.txt";

        if (args.length < 3) {
            System.out.println("Недостаточное количество параметров" + INFO);
            System.exit(0);
        }
        while (index < 2) {
            if (args[index].equals("-a") && !modeSet) {
                modeSet = true;
                index++;
            } else if (args[index].equals("-d") && !modeSet) {
                mode = SortingMode.DESC;
                modeSet = true;
                index++;
            } else if (args[index].equals("-i") && !typeSet) {
                type = DataType.INTEGER;
                index++;
                typeSet = true;
            } else if (args[index].equals("-s") && !typeSet) {
                type = DataType.STRING;
                typeSet = true;
                index++;
            } else if (index > 0) {
                if (!typeSet) {
                    System.out.println("Не задан тип входных данных! " + INFO);
                    System.exit(0);
                } else {
                    break;
                }
            } else {
                System.out.println("'" + args[index] + "' - неверный параметр! " + INFO);
                System.exit(0);
            }
        }

        if (args[index].matches(".+\\.txt")) {
            outputFile = Paths.get(args[index]);
            index++;
        } else {
            System.out.println("Указан неверный тип файла. '" + args[index] +
                    "'. Необходим файл с расширением .txt" + INFO);
            System.exit(0);
        }
        while (index < args.length) {
            if (args[index].matches(".+\\.txt")) {
                inputFileList.add(Paths.get(args[index]));
                index++;
            } else {
                System.out.println("Указан неверный тип файла. '" + args[index] +
                        "'. Необходим файл с расширением .txt" + INFO);
                System.exit(0);
            }
        }
    }

    public SortingMode getMode() {
        return mode;
    }

    public DataType getType() {
        return type;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public List<Path> getInputFileList() {
        return inputFileList;
    }
}
