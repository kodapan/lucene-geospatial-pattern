package se.kodapan.lucene.geospatial;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;

/**
 * @author kalle
 * @since 2015-10-28 10:37
 */
public class DocumentBuilder {

  private IndexFields indexFields;

  public DocumentBuilder() {
  }

  public DocumentBuilder(IndexFields indexFields) {
    this.indexFields = indexFields;
  }

  public void addBoundingBoxFields(BoundingBox boundingBox, Document document) {

    if (new InternationalDateLine().spans(boundingBox)) {
      throw new IllegalArgumentException("Bounding box spans the international date line. Needs to be split in two documents! See InternationalDateLine#split");
    }

    document.add(new DoubleField(indexFields.getSouth().getName(), boundingBox.getSouthLatitude(), indexFields.getSouth().getFieldType()));
    document.add(new DoubleField(indexFields.getWest().getName(), boundingBox.getWestLongitude(), indexFields.getWest().getFieldType()));
    document.add(new DoubleField(indexFields.getNorth().getName(), boundingBox.getNorthLatitude(), indexFields.getNorth().getFieldType()));
    document.add(new DoubleField(indexFields.getEast().getName(), boundingBox.getEastLongitude(), indexFields.getEast().getFieldType()));

  }

  public IndexFields getIndexFields() {
    return indexFields;
  }

  public DocumentBuilder setIndexFields(IndexFields indexFields) {
    this.indexFields = indexFields;
    return this;
  }
}
