package python.execution;

/**
 * Class for representation of a statement execution result
 */
public final class Result {
    /**
     * Type of result, can be "OK" or "EXCEPTIO"
     */
    public ResultType type;
    /**
     * Python process output
     */
    public String message;
}
