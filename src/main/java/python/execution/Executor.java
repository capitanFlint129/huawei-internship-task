package python.execution;


/**
 * Interface for python executor
 */
public interface Executor {
    /**
     * @param statement python statement for execution
     * @return result of execution, contains result type and python's output
     */
    Result execute(String statement);

    /**
     * Finish execution
     */
    void exit();
}
