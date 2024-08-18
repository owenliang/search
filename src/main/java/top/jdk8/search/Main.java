package top.jdk8.search;

public class Main {
    public static void main(String[] args) throws Exception {
        // 索引库
        String dbpath = args[0];
        IndexDB indexDB = new IndexDB(dbpath);

        // 构建索引
        InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder(indexDB);
        invertedIndexBuilder.addDocument("1", "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。");
        invertedIndexBuilder.addDocument("2", "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作");
        invertedIndexBuilder.addDocument("3", "黑夜很黑");
        invertedIndexBuilder.flush();

        // 搜索
    }
}