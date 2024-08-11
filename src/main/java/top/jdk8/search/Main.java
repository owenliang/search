package top.jdk8.search;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbpath=args[0];
        IndexDB db = new IndexDB(dbpath);
    }
}
