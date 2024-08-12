package top.jdk8.search;

import top.jdk8.search.protobuf.SearchProto;

public class DocumentHandle {
    private IndexDB indexDB;
    private String docId;
    private SearchProto.Document document;

    public static DocumentHandle loadFromDB(IndexDB indexDB, String docId) throws Exception {
        SearchProto.Document document = indexDB.getDocument(docId);
        if(document == null) {
            return null;
        }
        return new DocumentHandle(indexDB,docId,document);
    }

    public DocumentHandle(IndexDB indexDB, String docId, SearchProto.Document document) {
        this.indexDB = indexDB;
        this.docId = docId;
        this.document = document;
    }

    public void flush() throws Exception {
        indexDB.addDocument(docId,document);
    }
}
