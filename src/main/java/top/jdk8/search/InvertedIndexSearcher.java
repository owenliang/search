package top.jdk8.search;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import top.jdk8.search.protobuf.SearchProto;

import java.util.*;

public class InvertedIndexSearcher {
    private static class TermHit {
        private String term;
        private float tf;

        public TermHit(String term, float tf) {
            this.term = term;
            this.tf = tf;
        }

        public String toString() {
            return "TermHit{" +
                    "term='" + term + '\'' +
                    ", tf=" + tf +
                    '}';
        }
    }

    public static class ScoredDoc implements Comparable<ScoredDoc> {
        private String docId;
        private float score;
        private List<TermHit> termHits;

        public ScoredDoc(String docId, float score, List<TermHit> termHits) {
            this.docId = docId;
            this.score = score;
            this.termHits = termHits;
        }

        public int compareTo(ScoredDoc o) {
            return Float.compare(o.score, this.score);
        }

        public String toString() {
            return "ScoredDoc{" +
                    "docId='" + docId + '\'' +
                    ", score=" + score +
                    ", termHits=" + termHits +
                    '}';
        }
    }

    private IndexDB indexDB;

    public InvertedIndexSearcher(IndexDB indexDB) {
        this.indexDB = indexDB;
    }

    public List<ScoredDoc> search(String query, int topN) throws Exception {
        // 分词
        List<SegToken> tokens = tokenizer(query);
        // 加载倒排
        Map<String, PostingListHandle> term2PostingListHandle = new HashMap<>();   // 每个term的倒排
        Map<String, Integer> queryTermCount = new HashMap<>(); // 每个term在query中出现次数
        Map<String, List<TermHit>> docHits = new HashMap<>(); // 每个doc命中了哪些term
        Map<String, Integer> termDocTotal = new HashMap<>();   // 每个term出现在全局几个doc中(idf用)
        for (SegToken token : tokens) {
            if (term2PostingListHandle.containsKey(token.word)) {
                queryTermCount.put(token.word, queryTermCount.get(token.word) + 1);
                continue;
            }

            PostingListHandle postingListHandle = PostingListHandle.loadFromDB(indexDB, token.word);
            if (postingListHandle == null) {
                continue;
            }
            term2PostingListHandle.put(token.word, postingListHandle);
            queryTermCount.put(token.word, 1);

            for (Map.Entry<String, SearchProto.PostingItem> item : postingListHandle.getPostingList().entrySet()) {
                String docId = item.getKey();
                SearchProto.PostingItem postingItem = item.getValue();
                termDocTotal.put(token.word, postingListHandle.getPostingList().size());
                TermHit hit = new TermHit(token.word, postingItem.getTf());
                if (docHits.containsKey(docId)) {
                    docHits.get(docId).add(hit);
                } else {
                    List<TermHit> hits = new ArrayList<>();
                    hits.add(hit);
                    docHits.put(docId, hits);
                }
            }
        }
        // 算分
        int totalDocCount = indexDB.getDocCount();
        List<ScoredDoc> scoredDocs = new ArrayList<>();
        for (Map.Entry<String, List<TermHit>> entry : docHits.entrySet()) {
            String docId = entry.getKey();
            List<TermHit> termHits = entry.getValue();
            float score = 0;
            for (TermHit termHit : termHits) {
                float tf = termHit.tf;
                float idf = (float) Math.log(1.0 * totalDocCount / termDocTotal.get(termHit.term));
                score += tf * idf * queryTermCount.get(termHit.term);
            }
            scoredDocs.add(new ScoredDoc(docId, score, termHits));
        }
        Collections.sort(scoredDocs);
        return scoredDocs.subList(0, Math.min(topN, scoredDocs.size()));
    }

    public List<SegToken> tokenizer(String query) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> tokens = segmenter.process(query, JiebaSegmenter.SegMode.SEARCH);
        return tokens;
    }
}
