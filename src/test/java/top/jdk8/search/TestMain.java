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
        invertedIndexBuilder.addDocument("1", "在项目的开发过程中，我们经常需要使用Java来实现核心功能模块，这种情况下，选择合适的数据结构和算法至关重要。");
        invertedIndexBuilder.addDocument("2", "当面临性能优化的问题时，我们通常会从减少不必要的数据库查询开始，比如通过缓存机制或者优化Java代码中的循环和条件判断逻辑。");
        invertedIndexBuilder.addDocument("3", "为了确保Java应用程序的质量，持续集成流程中包含了自动化测试，这些测试覆盖了单元测试、集成测试等多个层面，有助于及时发现并修复缺陷。");
        invertedIndexBuilder.flush();
    }

    @Test
    public void testSearcher() throws Exception {
        IndexDB indexDB = new IndexDB("./test.db");

        InvertedIndexSearcher invertedIndexSearcher = new InvertedIndexSearcher(indexDB);
        List<InvertedIndexSearcher.ScoredDoc> scoredDocs = invertedIndexSearcher.search("Java算法", 2);

        System.out.println(scoredDocs);
    }
}
