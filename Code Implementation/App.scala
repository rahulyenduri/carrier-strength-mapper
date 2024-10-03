package edu.ucr.cs.cs226.dpeta002

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import edu.ucr.cs.bdlab.beast._
import edu.ucr.cs.bdlab.beast.geolite.{Feature, IFeature}
import edu.ucr.cs.bdlab.beast.synopses.Summary
import org.apache.spark.rdd.RDD
import edu.ucr.cs.bdlab.beast.cg.SpatialJoinAlgorithms.{ESJDistributedAlgorithm, ESJPredicate}
import edu.ucr.cs.bdlab.beast.indexing.{IndexHelper, KDTreePartitioner, RSGrovePartitioner, STRPartitioner}

/**
 * @author ${user.name}
 */
object App {

  def main (args : Array[String]) {

    val conf = new SparkConf().setAppName("Newtork_coverage_mapper").setMaster("local").set("spark.driver.memory", "2g").set("spark.executor.memory", "2g").set("spark.executor.cores", "2").set("spark.executor.instances", "1")
    val spark = SparkSession
      .builder()
      .config(conf)
      .getOrCreate()

    try {
      //Load the Riverside County Parcel GeoJSON file and apply filters to accept only valid geometry data
      val parcel_data: RDD[IFeature] = spark.sparkContext.geojsonFile("Riverside_Parcel_21.geojson.gz")
      .filter(f => f.getGeometry != null && !f.getGeometry.isEmpty && f.getGeometry.getEnvelopeInternal != null && f.getGeometry.isValid)

      //Partition the parcel data using RSGrovePartitioner. The other partitioning methods used for evaluation is commented.
      val partitioned_parcel: RDD[IFeature] = parcel_data.spatialPartition(classOf[RSGrovePartitioner])
      //val partitioned_parcel: RDD[IFeature] = parcel_data.spatialPartition(classOf[STRPartitioner])
      //val partitioned_parcel: RDD[IFeature] = parcel_data.spatialPartition(classOf[KDTreePartitioner])

      val summary1: Summary = parcel_data.summary
      println(summary1)

      //Load the Network Coverage GeoJSON file and apply filters to accept only valid geometry data.
      val voice_data: RDD[IFeature] = spark.sparkContext.geojsonFile("F477_Voice_1412_21.geojson.gz")
        .filter(f => f.getGeometry != null && !f.getGeometry.isEmpty && f.getGeometry.getEnvelopeInternal != null && f.getGeometry.isValid)

      //Partition the network coverage data using RSGrovePartitioner.
      val partitioned_voice: RDD[IFeature] = voice_data.spatialPartition(classOf[RSGrovePartitioner])
      //val partitioned_voice: RDD[IFeature] = voice_data.spatialPartition(classOf[STRPartitioner])
      //val partitioned_voice: RDD[IFeature] = voice_data.spatialPartition(classOf[KDTreePartitioner])

      val summary: Summary = voice_data.summary
      println(summary)

      //Spatial Join operation to find the parcels contained inside the network region covered by carriers.
      //Predicate contains is used in the join.
      val sjResults: RDD[(IFeature, IFeature)] = partitioned_voice.spatialJoin(partitioned_parcel, ESJPredicate.Contains)

      //Create a column order mapping to be used later
      val columns_order: Map[String, Int] = Map("object_id" -> 1, "apt_no" -> 2, "mail_street" -> 4, "mail_city" -> 5, "street_number" -> 8, "street_name" -> 10, "city" -> 12, "zipcode" -> 13, "primary_owner" -> 29)

      //The result of the join is of type RDD[(IFeature, IFeature)]. This is flattened.
      //The geometry column from network coverage data is no longer needed. The geometry of the parcel will suffice for visualization. Hence, it is discarded.
      val PowerNetworkRDD = sjResults.map(f => (f._2.get(columns_order.get("object_id").getOrElse(0)), (try {f._1.get(2).toString.asInstanceOf[Int]} catch {case _: ClassCastException => 0}, f._1.get(1), f._2.get(columns_order.get("apt_no").getOrElse(0)), f._2.get(columns_order.get("mail_street").getOrElse(0)), f._2.get(columns_order.get("mail_city").getOrElse(0)), f._2.get(columns_order.get("street_number").getOrElse(0)), f._2.get(columns_order.get("street_name").getOrElse(0)), f._2.get(columns_order.get("city").getOrElse(0)) )))

      //The carrier with best signal strength for each parcel is found using reduceByKey, where key is the object_id (Unique ID for each parcel)
      val maxPowerNetworkRDD = PowerNetworkRDD.reduceByKey {
        case ((power1, networkName1, apt1, mail_street1, mail_city1, street_number1, street_name1, city1 ), (power2, networkName2, apt2, mail_street2, mail_city2, street_number2, street_name2, city2)) =>
          if (power1> power2) (power1, networkName1, apt1, mail_street1, mail_city1, street_number1, street_name1, city1) else (power2, networkName2, apt2, mail_street2, mail_city2, street_number2, street_name2, city2)
      }

      //Write the result to a file.
      maxPowerNetworkRDD.saveAsTextFile("join_output_24")

    } finally {spark.sparkContext.stop()}
  }
}