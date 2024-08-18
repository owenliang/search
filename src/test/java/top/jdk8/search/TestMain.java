package top.jdk8.search;

import org.junit.jupiter.api.Test;

import java.util.List;

public class TestMain {
    // 写一个单测示例

    @Test
    public void testBuilder() throws Exception {
        // 索引库
        IndexDB indexDB = new IndexDB("./test.db");

        // 构建索引
        InvertedIndexBuilder invertedIndexBuilder = new InvertedIndexBuilder(indexDB);
        invertedIndexBuilder.addDocument("1", "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。");
        invertedIndexBuilder.addDocument("2", "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作");
        invertedIndexBuilder.addDocument("3", "黑夜很黑");
        invertedIndexBuilder.flush();
    }

    @Test
    public void testSearcher() throws Exception {
        IndexDB indexDB = new IndexDB("./test.db");

        InvertedIndexSearcher invertedIndexSearcher = new InvertedIndexSearcher(indexDB);
        List<InvertedIndexSearcher.ScoredDoc> scoredDocs = invertedIndexSearcher.search("黑夜给了我黑色的眼睛", 1);

        System.out.println(scoredDocs);
    }
}
