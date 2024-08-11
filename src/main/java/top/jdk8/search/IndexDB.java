package top.jdk8.search;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import top.jdk8.search.protobuf.SearchProto;


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
    public void addDocument(String docId, SearchProto.Document document) {

    }
    public SearchProto.Document getDocument(String docId) {
        return null;
    }

    // 倒排
    public void addPostingList(SearchProto.PostingList postingList) {

    }
    public SearchProto.PostingList getPostingList(String term) {
        return null;
    }
}
