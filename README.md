# lucene-geospatial-pattern

Is this optimal? Doubtful. Does it work? Yes. Is it fast enough? For me it is more than enough.

This is a pattern that allows for geospatial queries in Lucene. Geometries in the index are represented as the envelope
boundary box. Queries are also translated to the envelope boundary box. This makes it easy to match the query with index
for geometries that intersect, are completely covered by query or index shape.

Of course, this means that you might be getting matches from the index that does in fact not geometrically match. If this
is very important to you, you will have to post process your responses with more expensive calculations. Example:

![Image of indexed line and query](https://raw.githubusercontent.com/kodapan/lucene-geospatial-pattern/master/documentation/indexed%20line%20and%20query.png)

This code also support geometries and queries that span the international date line by cutting such queries in two.
