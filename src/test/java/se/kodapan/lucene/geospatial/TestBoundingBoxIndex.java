package se.kodapan.lucene.geospatial;

import junit.framework.TestCase;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kalle
 * @since 2015-10-28 11:37
 */
public class TestBoundingBoxIndex extends TestCase {

  private IndexFields indexFields;

  private Directory directory;
  private IndexWriter indexWriter;
  private SearcherManager searcherManager;

  @Override
  protected void setUp() throws Exception {
    indexFields = new IndexFields();

    directory = new RAMDirectory();
    indexWriter = new IndexWriter(directory, new IndexWriterConfig(new KeywordAnalyzer()));
    searcherManager = new SearcherManager(indexWriter, true, new SearcherFactory());

    populateIndex();
  }

  @Override
  protected void tearDown() throws Exception {
    searcherManager.close();
    indexWriter.close();
    directory.close();
  }

  public void populateIndex() throws IOException {

    index(new BoundingBox(10d, 10d, 20d, 20d), "a");
    index(new BoundingBox(10d, 30d, 20d, 40d), "b");
    index(new BoundingBox(30d, 10d, 40d, 20d), "c");
    index(new BoundingBox(30d, 30d, 40d, 40d), "d");
    indexWriter.commit();
    searcherManager.maybeRefreshBlocking();

  }

  public void index(BoundingBox boundingBox, String name) throws IOException {
    Document document = new Document();
    document.add(new StringField("name", name, Field.Store.YES));
    new DocumentBuilder(indexFields).addBoundingBoxFields(boundingBox, document);
    indexWriter.addDocument(document);
  }

  public Set<String> search(Query query) throws IOException {
    final Set<String> hits = new HashSet<>();
    IndexSearcher searcher = searcherManager.acquire();
    try {
      searcher.search(query, new Collector() {
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
          return new LeafCollector() {
            @Override
            public void setScorer(Scorer scorer) throws IOException {
            }

            @Override
            public void collect(int doc) throws IOException {
              hits.add(context.reader().document(doc).get("name"));
            }
          };
        }

        @Override
        public boolean needsScores() {
          return false;
        }
      });
    } finally {
      searcherManager.release(searcher);
    }
    return hits;
  }

  public void test() throws Exception {

    // covers all
    BoundingBox coversAll = new BoundingBox(5d, 5d, 45d, 45d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(coversAll)), "a", "b", "c", "d");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(coversAll)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(coversAll)), "a", "b", "c", "d");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(coversAll).setIndexFields(indexFields).build()), "a", "b", "c", "d");


    // covers A, B, C and D
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(new BoundingBox(10d, 10d, 20d, 20d)).setIndexFields(indexFields).build()), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(new BoundingBox(10d, 30d, 20d, 40d)).setIndexFields(indexFields).build()), "b");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(new BoundingBox(30d, 10d, 40d, 20d)).setIndexFields(indexFields).build()), "c");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(new BoundingBox(30d, 30d, 40d, 40d)).setIndexFields(indexFields).build()), "d");


    // covers A
    BoundingBox coversA = new BoundingBox(9d, 9d, 21d, 21d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(coversA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(coversA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(coversA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(coversA).setIndexFields(indexFields).build()), "a");

    // inside A
    BoundingBox insideA = new BoundingBox(11d, 11d, 19d, 19d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(insideA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(insideA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(insideA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(insideA).setIndexFields(indexFields).build()), "a");


    // intersects south A
    BoundingBox intersectsSouthA = new BoundingBox(5d, 12d, 15d, 17d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(intersectsSouthA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(intersectsSouthA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(intersectsSouthA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(intersectsSouthA).setIndexFields(indexFields).build()), "a");

    // intersects west A
    BoundingBox intersectsWestA = new BoundingBox(12d, 5d, 15d, 17d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(intersectsWestA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(intersectsWestA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(intersectsWestA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(intersectsWestA).setIndexFields(indexFields).build()), "a");

    // intersects north A
    BoundingBox intersectsNorthA = new BoundingBox(12d, 12d, 22d, 17d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(intersectsNorthA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(intersectsNorthA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(intersectsNorthA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(intersectsNorthA).setIndexFields(indexFields).build()), "a");


    // intersects east A
    BoundingBox intersectsEastA = new BoundingBox(12d, 12d, 15d, 22d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(intersectsEastA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(intersectsEastA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(intersectsEastA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(intersectsEastA).setIndexFields(indexFields).build()), "a");

    // intersects northeast A
    BoundingBox intersectsNortheastA = new BoundingBox(15d, 15d, 25d, 25d);
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildCoversIndexBoundingBox(intersectsNortheastA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIndexBoundingBoxCovers(intersectsNortheastA)));
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(null).setIndexFields(indexFields).buildIntersectsIndexBoundingBox(intersectsNortheastA)), "a");
    assertHitsEquals(search(new BoundingBoxQueryBuilder().setBoundingBox(intersectsNortheastA).setIndexFields(indexFields).build()), "a");


  }

  private void assertHitsEquals(Collection<String> hits, String... values) {
    if (values.length != hits.size()) {
      fail(hits.toString());
    }
    Set<String> valuesSet = new HashSet<>(Arrays.asList(values));
    assertEquals(hits, valuesSet);
  }

}
