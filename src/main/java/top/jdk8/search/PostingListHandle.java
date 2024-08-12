package top.jdk8.search;

import top.jdk8.search.protobuf.SearchProto;
import java.util.concurrent.ConcurrentSkipListMap;

public class PostingListHandle {
    private IndexDB indexDB;
    private String term;
    // doc_id -> posting_item
    private ConcurrentSkipListMap<Long, SearchProto.PostingItem> postingList = new ConcurrentSkipListMap<>();

    public static PostingListHandle loadFromDB(IndexDB indexDB, String term) throws Exception {
        SearchProto.PostingList postingList = indexDB.getPostingList(term);
        return new PostingListHandle(indexDB,term,postingList);
    }

    public PostingListHandle(IndexDB indexDB,String term, SearchProto.PostingList postingList) {
        this.indexDB=indexDB;
        this.term = term;
        for(int i=0;i<postingList.getItemsCount();++i) {
            SearchProto.PostingItem postingItem = postingList.getItems(i);
            this.postingList.put(postingItem.getDocId(),postingItem);
        }
    }

    public PostingListHandle(IndexDB indexDB,String term) {
        this.indexDB=indexDB;
        this.term=term;
    }

    // term关联到1个新的doc
    public void addPostingItem(SearchProto.PostingItem postingItem) {
        this.postingList.put(postingItem.getDocId(),postingItem);
    }

    public void flushUpdateToDB() {
        // 从DB读取已有的拉链
        SearchProto.PostingList curPostingList = indexDB.getPostingList(term);

        // 存量+增量merge到一起
        for(int i=0;i<curPostingList.getItemsCount();++i) {
            SearchProto.PostingItem postingItem = curPostingList.getItems(i);
            this.postingList.put(postingItem.getDocId(),postingItem);
        }

        // 生成dump数据
        SearchProto.PostingList.Builder postingListBuilder =  SearchProto.PostingList.newBuilder();
        for (SearchProto.PostingItem item : this.postingList.values()) {
            postingListBuilder.addItems(item);
        }

        // 刷入DB
        indexDB.addPostingList(term,postingListBuilder.build());
    }
}
