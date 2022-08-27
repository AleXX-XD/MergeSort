public class Main {

    public static void main(String[] args) {
            ArgumentsHandler argsHandler = new ArgumentsHandler();
            argsHandler.checkingParameters(args);
            FilesHandler filesHandler = new FilesHandler(argsHandler.getInputFileList(),
                    argsHandler.getMode(), argsHandler.getType(), argsHandler.getOutputFile());
            filesHandler.startProcessing();
    }
}
