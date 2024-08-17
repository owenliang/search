package top.jdk8.search;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import top.jdk8.search.protobuf.SearchProto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndexBuilder {
    private IndexDB indexDB;
    private Map<String,PostingListHandle> buildBuffer = new HashMap<>();

    public InvertedIndexBuilder(IndexDB indexDB) {
        this.indexDB = indexDB;
    }
    public void addDocument(String docId,String content) throws Exception {
        SearchProto.Document.Builder documentBuilder = SearchProto.Document.newBuilder();
        documentBuilder.setContent(content);

        // 分词
        tokenizer(content,documentBuilder);
        SearchProto.Document document = documentBuilder.build();

        // 正排入库
        DocumentHandle documentHandle = DocumentHandle.loadFromDB(indexDB,docId);
        if(documentHandle!=null) {  // 不支持更新
            return;
        }
        documentHandle = new DocumentHandle(indexDB,docId,document);
        documentHandle.flush();

        // 词频统计
        Map<String,Integer> termFreqMap = new HashMap<>();
        for (int i=0;i<document.getTermsCount();i++) {
            String term=document.getTerms(i).getTerm();
            Integer freq=termFreqMap.get(term);
            if (freq==null){
                freq=0;
            }
            termFreqMap.put(term,freq+1);
        }

        // doc倒排写入缓冲区
        SearchProto.PostingItem.Builder postingItemBuilder = SearchProto.PostingItem.newBuilder();
        for (Map.Entry<String,Integer> pair:termFreqMap.entrySet()) {
            String term = pair.getKey();
            Integer freq = pair.getValue();
            PostingListHandle postingListHandle = buildBuffer.get(term);
            if(postingListHandle==null){
                postingListHandle=new PostingListHandle(indexDB,term);
                buildBuffer.put(term,postingListHandle);
            }
            postingItemBuilder.clear().setDocId(docId).setTf((float) freq /document.getTermsCount());
            postingListHandle.addPostingItem(postingItemBuilder.build());
        }
    }

    public void tokenizer(String content, SearchProto.Document.Builder documentBuilder) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> tokens=segmenter.process(content, JiebaSegmenter.SegMode.INDEX);

        SearchProto.TermInfo.Builder termInfoBuilder = SearchProto.TermInfo.newBuilder();
        for (SegToken token : tokens) {
            SearchProto.TermInfo termInfo = termInfoBuilder.clear()
                    .setTerm(token.word)
                    .setOffset(token.startOffset)
                    .setLength(token.endOffset-token.startOffset)
                    .build();
            documentBuilder.addTerms(termInfo);
        }
    }

    public void flush() throws Exception {
        for (Map.Entry<String,PostingListHandle> pair:buildBuffer.entrySet()) {
            PostingListHandle postingListHandle = pair.getValue();
            postingListHandle.flush();
        }
        buildBuffer.clear();
    }
}
