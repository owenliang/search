# 倒排索引

简单的倒排索引原理示例。

* 离线构造倒排索引，jieba分词，snappy压缩，protobuf序列化，持久化到rocksdb
* 在线召回倒排链表，tf*idf计算文档匹配度

## 离线构建索引

```
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
```

这里索引3篇文档，基于rocksdb持久化到磁盘。

## 在线检索

```
    @Test
    public void testSearcher() throws Exception {
        IndexDB indexDB = new IndexDB("./test.db");

        InvertedIndexSearcher invertedIndexSearcher = new InvertedIndexSearcher(indexDB);
        List<InvertedIndexSearcher.ScoredDoc> scoredDocs = invertedIndexSearcher.search("Java算法", 2);

        System.out.println(scoredDocs);
    }
```

返回topN打分文档，以及term命中信息:
```
[
ScoredDoc{docId='1', score=0.031388924, termHits=[TermHit{term='java', tf=0.028571429}, TermHit{term='算法', tf=0.028571429}]}, 
ScoredDoc{docId='2', score=0.0, termHits=[TermHit{term='java', tf=0.025641026}]}
]

```

## 参考书籍

《自制搜索引擎》