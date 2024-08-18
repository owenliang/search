package top.jdk8.search;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.xerial.snappy.Snappy;
import top.jdk8.search.protobuf.SearchProto;

import java.nio.charset.StandardCharsets;


public class IndexDB {
    // Key类型
    public enum DBKeyType {
        PREFIX_DOCUMENT((byte) 0),
        PREFIX_POSTING_LIST((byte) 1),
        PREFIX_POSTING_STATS((byte) 3);

        private byte value;

        DBKeyType(byte value) {
            this.value = value;
        }

        public byte[] buildKey(String ukey) {
            byte[] ukey_bytes = ukey.getBytes(StandardCharsets.UTF_8);
            byte[] key = new byte[1 + ukey_bytes.length];
            key[0] = value;
            System.arraycopy(ukey_bytes, 0, key, 1, ukey_bytes.length);
            return key;
        }
    }

    private RocksDB db;

    public IndexDB(String dbpath) throws Exception {
        RocksDB.loadLibrary();
        try (Options options = new Options()) {
            options.setCreateIfMissing(true);
            db = RocksDB.open(options, dbpath);
        }
    }

    // 文档总数
    public int getDocCount() throws Exception {
        int docCount = 0;
        byte[] bytes = db.get(DBKeyType.PREFIX_POSTING_STATS.buildKey("docCount"));
        if (bytes != null) {
            docCount = Integer.parseInt(new String(bytes, StandardCharsets.UTF_8));
        }
        return docCount;
    }

    // 正排
    public void addDocument(String docId, SearchProto.Document document) throws Exception {
        byte[] bytes = document.toByteArray();
        db.put(DBKeyType.PREFIX_DOCUMENT.buildKey(docId), Snappy.compress(bytes));

        int docCount = getDocCount();
        docCount += 1;
        db.put(DBKeyType.PREFIX_POSTING_STATS.buildKey("docCount"), Integer.toString(docCount).getBytes(StandardCharsets.UTF_8));
    }

    public SearchProto.Document getDocument(String docId) throws Exception {
        byte[] bytes = db.get(DBKeyType.PREFIX_POSTING_LIST.buildKey(docId));
        if (bytes == null) {
            return null;
        }
        return SearchProto.Document.parseFrom(Snappy.uncompress(bytes));
    }

    // 倒排
    public void addPostingList(String term, SearchProto.PostingList postingList) throws Exception {
        byte[] bytes = postingList.toByteArray();
        db.put(DBKeyType.PREFIX_POSTING_LIST.buildKey(term), Snappy.compress(bytes));
    }

    public SearchProto.PostingList getPostingList(String term) throws Exception {
        byte[] bytes = db.get(DBKeyType.PREFIX_POSTING_LIST.buildKey(term));
        if (bytes == null) {
            return null;
        }
        return SearchProto.PostingList.parseFrom(Snappy.uncompress(bytes));
    }
}
