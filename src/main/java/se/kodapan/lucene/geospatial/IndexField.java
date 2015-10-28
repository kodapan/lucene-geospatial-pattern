package se.kodapan.lucene.geospatial;

import org.apache.lucene.document.FieldType;

/**
 * @author kalle
 * @since 2015-10-28 10:59
 */
public class IndexField {

  private String name;
  private FieldType fieldType;

  public IndexField() {
  }

  public IndexField(String name, FieldType fieldType) {
    this.name = name;
    this.fieldType = fieldType;
  }

  public FieldType getFieldType() {
    return fieldType;
  }

  public void setFieldType(FieldType fieldType) {
    this.fieldType = fieldType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
