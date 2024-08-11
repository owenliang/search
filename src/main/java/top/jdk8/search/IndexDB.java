package top.jdk8.search;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.xerial.snappy.Snappy;
import top.jdk8.search.protobuf.SearchProto;

import java.nio.charset.StandardCharsets;


public class IndexDB {
    private RocksDB db;

    public IndexDB(String dbpath) throws Exception {
        RocksDB.loadLibrary();
        try(Options options = new Options()) {
            options.setCreateIfMissing(true);
            db = RocksDB.open(options, dbpath);
        }
    }

    // 正排
    public void addDocument(String docId, SearchProto.Document document) throws Exception {
        byte[] bytes = document.toByteArray();
        // System.out.println(bytes.length);
        db.put(docId.getBytes(StandardCharsets.UTF_8), Snappy.compress(bytes));
    }
    public SearchProto.Document getDocument(String docId) throws Exception {
        byte[] bytes =db.get(docId.getBytes(StandardCharsets.UTF_8));
        // System.out.println(bytes.length);
        return SearchProto.Document.parseFrom(Snappy.uncompress(bytes));
    }

    // 倒排
    public void addPostingList(String term,SearchProto.PostingList postingList) {

    }
    public SearchProto.PostingList getPostingList(String term) {
        return null;
    }
}
