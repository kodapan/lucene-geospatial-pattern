package se.kodapan.lucene.geospatial;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 * @author kalle
 * @since 2015-10-28 10:58
 */
public class IndexFields {

  private IndexField south = new IndexField("south", fieldTypeFactory(8));
  private IndexField west = new IndexField("west", fieldTypeFactory(8));
  private IndexField north = new IndexField("north", fieldTypeFactory(8));
  private IndexField east = new IndexField("east", fieldTypeFactory(8));

  private FieldType fieldTypeFactory(int numericPrecisionStep) {
    FieldType fieldType = new FieldType();
    fieldType.setStored(false);
    fieldType.setOmitNorms(true);
    fieldType.setTokenized(true);

    fieldType.setIndexOptions(IndexOptions.DOCS);
    fieldType.setNumericType(FieldType.NumericType.DOUBLE);
    fieldType.setNumericPrecisionStep(numericPrecisionStep);
    fieldType.freeze();
    return fieldType;
  }

  public IndexField getSouth() {
    return south;
  }

  public void setSouth(IndexField south) {
    this.south = south;
  }

  public IndexField getWest() {
    return west;
  }

  public void setWest(IndexField west) {
    this.west = west;
  }

  public IndexField getNorth() {
    return north;
  }

  public void setNorth(IndexField north) {
    this.north = north;
  }

  public IndexField getEast() {
    return east;
  }

  public void setEast(IndexField east) {
    this.east = east;
  }
}
