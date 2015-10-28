package se.kodapan.lucene.geospatial;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

/**
 * @author kalle
 * @since 2015-10-28 10:42
 */
public class BoundingBoxQueryBuilder {

  private IndexFields indexFields;
  private BoundingBox boundingBox;

  public BoundingBoxQueryBuilder() {
  }

  public BoundingBoxQueryBuilder(IndexFields indexFields, BoundingBox boundingBox) {
    this.indexFields = indexFields;
    this.boundingBox = boundingBox;
  }

  public Query build() {

    BoundingBox[] parts = new InternationalDateLine().split(getBoundingBox());
    BooleanQuery.Builder partQueriesBuilder = new BooleanQuery.Builder();
    for (BoundingBox part : parts) {
      partQueriesBuilder.add(new BooleanClause(build(part), BooleanClause.Occur.SHOULD));
    }
    return partQueriesBuilder.build();

  }

  public Query build(BoundingBox boundingBox) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BooleanClause(buildIntersectsIndexBoundingBox(boundingBox), BooleanClause.Occur.SHOULD));
    // not required, will also be matched by intersection
//    builder.add(new BooleanClause(buildCoversIndexBoundingBox(boundingBox), BooleanClause.Occur.SHOULD));
    builder.add(new BooleanClause(buildIndexBoundingBoxCovers(boundingBox), BooleanClause.Occur.SHOULD));
    return builder.build();
  }

  public Query buildIntersectsIndexBoundingBox(BoundingBox boundingBox) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getSouth().getName(), indexFields.getSouth().getFieldType().numericPrecisionStep(), -90d, boundingBox.getNorthLatitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getWest().getName(), indexFields.getWest().getFieldType().numericPrecisionStep(), -180d, boundingBox.getEastLongitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getNorth().getName(), indexFields.getNorth().getFieldType().numericPrecisionStep(), boundingBox.getSouthLatitude(), 90d, true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getEast().getName(), indexFields.getEast().getFieldType().numericPrecisionStep(), boundingBox.getWestLongitude(), 180d, true, true), BooleanClause.Occur.MUST));
    return builder.build();
  }

  public Query buildCoversIndexBoundingBox(BoundingBox boundingBox) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getSouth().getName(), indexFields.getSouth().getFieldType().numericPrecisionStep(), boundingBox.getSouthLatitude(), boundingBox.getNorthLatitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getWest().getName(), indexFields.getWest().getFieldType().numericPrecisionStep(), boundingBox.getWestLongitude(), boundingBox.getEastLongitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getNorth().getName(), indexFields.getNorth().getFieldType().numericPrecisionStep(), boundingBox.getSouthLatitude(), boundingBox.getNorthLatitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getEast().getName(), indexFields.getEast().getFieldType().numericPrecisionStep(), boundingBox.getWestLongitude(), boundingBox.getEastLongitude(), true, true), BooleanClause.Occur.MUST));
    return builder.build();
  }

  public Query buildIndexBoundingBoxCovers(BoundingBox boundingBox) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getSouth().getName(), indexFields.getSouth().getFieldType().numericPrecisionStep(), -90d, boundingBox.getSouthLatitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getWest().getName(), indexFields.getWest().getFieldType().numericPrecisionStep(), -180d, boundingBox.getWestLongitude(), true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getNorth().getName(), indexFields.getNorth().getFieldType().numericPrecisionStep(), boundingBox.getNorthLatitude(), 90d, true, true), BooleanClause.Occur.MUST));
    builder.add(new BooleanClause(NumericRangeQuery.newDoubleRange(indexFields.getEast().getName(), indexFields.getEast().getFieldType().numericPrecisionStep(), boundingBox.getEastLongitude(), 180d, true, true), BooleanClause.Occur.MUST));
    return builder.build();
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

  public BoundingBoxQueryBuilder setBoundingBox(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;
    return this;
  }

  public IndexFields getIndexFields() {
    return indexFields;
  }

  public BoundingBoxQueryBuilder setIndexFields(IndexFields indexFields) {
    this.indexFields = indexFields;
    return this;
  }
}
