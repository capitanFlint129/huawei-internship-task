package example;

import python.execution.Executor;
import python.execution.Result;
import python.execution.SubprocessInteractiveExecutor;

import java.nio.file.Paths;

public class Main {
    public static void messageExample() {
        Executor exe = new SubprocessInteractiveExecutor(Paths.get("python"));
        Result res = exe.execute("""
                'aaaaaaaa'""");
        System.out.println(res.type);
        System.out.println(res.message);
        exe.exit();
    }

    public static void exceptionExample() {
        Executor exe = new SubprocessInteractiveExecutor(Paths.get("python"));
        Result res = exe.execute("""
                1/0""");
        exe.exit();
        System.out.println(res.type);
        System.out.println(res.message);
    }

    public static void classExample() {
        Executor exe = new SubprocessInteractiveExecutor(Paths.get("python"));
        Result res = exe.execute("""
                class A:
                    a = 10""");
        System.out.println(res.type);
        System.out.println(res.message);
        res = exe.execute("""
                object = A()""");
        System.out.println(res.type);
        System.out.println(res.message);
        res = exe.execute("""
                object.a""");
        System.out.println(res.type);
        System.out.println(res.message);
        exe.exit();
    }
    public static void main(String[] args) {
        messageExample();
    }

}
