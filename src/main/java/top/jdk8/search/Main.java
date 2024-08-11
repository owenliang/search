package top.jdk8.search;

import top.jdk8.search.protobuf.SearchProto;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Main {
    public static void main(String[] args) throws Exception {
        String dbpath=args[0];
        IndexDB db = new IndexDB(dbpath);

        ////////// 写入
        SearchProto.Document.Builder docBuilder = SearchProto.Document.newBuilder();
        docBuilder.setContent("hello world");

        SearchProto.TermInfo.Builder termInfoBuilder = SearchProto.TermInfo.newBuilder();
        termInfoBuilder.setTerm("你好").setOffset(5).setLength(2);
        docBuilder.addTerms((termInfoBuilder.build()));
        SearchProto.Document document=docBuilder.build();

        db.addDocument("1",document);

        ////////// 查询
        SearchProto.Document doc = db.getDocument("1");
        System.out.println(doc);

        ///////// 倒排表（doc_id -> PostingItem）
    }
}