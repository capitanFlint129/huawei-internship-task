package python.execution;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SubprocessInteractiveExecutor implements Executor {
    private static final String PYTHON_FORCE_INTERACTIVE_MODE_FLAG = "-i";
    private static final String[] PYTHON_IMPORTS = {"traceback", "sys"};

    private final BufferedReader stdout;
    private final BufferedWriter stdin;
    private final Process pythonProcess;

    public SubprocessInteractiveExecutor(Path pathToPythonInterpreter, String... options) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Stream.of(new String[]{pathToPythonInterpreter.toString(), PYTHON_FORCE_INTERACTIVE_MODE_FLAG}, options).flatMap(Stream::of).toArray(String[]::new));
        try {
            pythonProcess = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stdin = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
        stdout = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));

        importLibraries();
    }

    @Override
    public Result execute(String statement) {
        writeStatement(String.format("""
                try:
                    %s
                    print('%s')
                except Exception as e:
                    traceback.print_exception(e, file=sys.stdout)
                    print('%s')

                """, prepareStatementForExecution(statement), ResultType.OK, ResultType.EXCEPTION));
        return getResult();
    }

    @Override
    public void exit() {
        pythonProcess.destroy();
    }

    private String prepareStatementForExecution(String statement) {
        return statement.replace("\n", "\n    ");
    }

    private void importLibraries() {
        for (String lib : PYTHON_IMPORTS) {
            writeStatement(String.format("""
                    import %s
                    """, lib));
        }
    }

    private void writeStatement(String statement) {
        try {
            stdin.write(statement + "\n");
            stdin.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Result getResult() {
        try {
            Result result = new Result();
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = stdout.readLine()) != null) {
                if (line.equals(ResultType.EXCEPTION.toString()) && !stdout.ready()) {
                    result.type = ResultType.EXCEPTION;
                    break;
                } else if (line.equals(ResultType.OK.toString()) && !stdout.ready()) {
                    result.type = ResultType.OK;
                    break;
                } else {
                    builder.append(line);
                    builder.append(System.getProperty("line.separator"));
                }
            }
            result.message = builder.toString();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
