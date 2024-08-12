package top.jdk8.search;

public class InvertedIndexBuilder {
    public static void addDocument(String docId,String content) {
        // 1，分词+正排入库
        // 2，分词统计词频
        // 3，倒排索引构建，合并到DB
    }
}
