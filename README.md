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
        invertedIndexBuilder.addDocument("1", "这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。");
        invertedIndexBuilder.addDocument("2", "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作");
        invertedIndexBuilder.addDocument("3", "黑夜很黑");
        invertedIndexBuilder.flush();
    }
```

这里索引3篇文档，基于rocksdb持久化到磁盘。

## 在线检索

```
    public void testSearcher() throws Exception {
        IndexDB indexDB = new IndexDB("./test.db");

        InvertedIndexSearcher invertedIndexSearcher = new InvertedIndexSearcher(indexDB);
        List<InvertedIndexSearcher.ScoredDoc> scoredDocs = invertedIndexSearcher.search("黑夜给了我黑色的眼睛", 2);

        System.out.println(scoredDocs);
    }
```

返回topN打分文档，以及term命中信息:
```
[
ScoredDoc{docId='1', score=0.17111531, termHits=[TermHit{term='黑夜', tf=0.041666668}, TermHit{term='我', tf=0.125}, TermHit{term='的', tf=0.041666668}]}, 
ScoredDoc{docId='3', score=0.13515504, termHits=[TermHit{term='黑夜', tf=0.33333334}]}
]
```