package se.kodapan.lucene.geospatial;

import java.io.Serializable;

/**
 * @author kalle
 * @since 2015-10-28 10:48
 */
public class BoundingBox implements Serializable {

  private static final long serialVersionUID = 1l;

  private Double southLatitude;
  private Double westLongitude;
  private Double northLatitude;
  private Double eastLongitude;

  public BoundingBox() {
  }

  public BoundingBox(Double southLatitude, Double westLongitude, Double northLatitude, Double eastLongitude) {
    this.southLatitude = southLatitude;
    this.westLongitude = westLongitude;
    this.northLatitude = northLatitude;
    this.eastLongitude = eastLongitude;
  }

  public Double getSouthLatitude() {
    return southLatitude;
  }

  public BoundingBox setSouthLatitude(Double southLatitude) {
    this.southLatitude = southLatitude;
    return this;
  }

  public Double getWestLongitude() {
    return westLongitude;
  }

  public BoundingBox setWestLongitude(Double westLongitude) {
    this.westLongitude = westLongitude;
    return this;
  }

  public Double getNorthLatitude() {
    return northLatitude;
  }

  public BoundingBox setNorthLatitude(Double northLatitude) {
    this.northLatitude = northLatitude;
    return this;
  }

  public Double getEastLongitude() {
    return eastLongitude;
  }

  public BoundingBox setEastLongitude(Double eastLongitude) {
    this.eastLongitude = eastLongitude;
    return this;
  }
}
