import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json


case class UserData(ssp: String, id: String, deviceOs: String, c: String, supplyType: String, supplyTag_tags: Integer, supplyTag_domain: String, consentMetadata_userConsent: Boolean, bidderVersion: String, ts_no_bid: Long, date: String, y: String, m: String, d: String, h: String)

object StreamHandler{
    def main(args: Array[String]){

        val spark = SparkSession
          .builder
          .master("local[*]")
          .appName("StreamHandler")
          .getOrCreate()
        
        import spark.implicits._ //help us avoid some warnings?
        

        val ReadSchema: StructType = new StructType()
            .add("ssp", StringType)
            .add("id", StringType)
            .add("deviceOs", StringType)
            .add("c", StringType)
            .add("supplyType", StringType)                              
            .add("supplyTag",
              new StructType()
                .add("tags", ArrayType(IntegerType))
                .add("domain", StringType))
            .add("consentMetadata",
              new StructType()
                .add("userConsent", BooleanType))
            .add("bidderVersion", StringType)
            .add("ts_no_bid",LongType)

        val inputDF = spark
          .readStream
          .format("kafka")
          .option("kafka.bootstrap.servers", "localhost:9092")
          .option("subscribe", "test-topic")
          //.option("fetchOffset.retryIntervalMs",10000)  //milliseconds to wait before retrying to fetch Kafka offsets
          .load()
          //.selectExpr("CAST(value AS STRING)").as[String]
          .select(from_json($"value".cast(StringType), ReadSchema).as("value")) //.cast(StringType)

        

        val prunedDF = inputDF.select(col("value").getItem("ssp") as "ssp", 
            col("value").getItem("id") as "id",
            col("value").getItem("deviceOs") as "deviceOs",
            col("value").getItem("c") as "c",
            col("value").getItem("supplyType") as "supplyType",
            $"value".getItem("supplyTag").getItem("tags")(0) as "supplyTag_tags",
            col("value").getItem("supplyTag").getItem("domain") as "supplyTag_domain",
            col("value").getItem("consentMetadata").getItem("userConsent") as "consentMetadata_userConsent",
            col("value").getItem("bidderVersion") as "bidderVersion",
            col("value").getItem("ts_no_bid") as "ts_no_bid")
            .withColumn("date", to_date(from_unixtime($"ts_no_bid" / 1000)))
            .withColumn("y", date_format(from_unixtime($"ts_no_bid" / 1000), "yyyy").cast(StringType))
            .withColumn("m", date_format(from_unixtime($"ts_no_bid" / 1000), "MM").cast(StringType))
            .withColumn("d", date_format(from_unixtime($"ts_no_bid" / 1000), "dd").cast(StringType))
            .withColumn("h", date_format(from_unixtime($"ts_no_bid" / 1000), "HH").cast(StringType))
            //https://mungingdata.com/apache-spark/filter-where/ 
            //HH:mm:ss
            .where("consentMetadata_userConsent = true")
            //.withWatermark(date_format(from_unixtime($"ts_no_bid" / 1000) ,"yyyy-MM-dd HH:mm:ss"), "10 seconds")



        // val query00 = prunedDF.writeStream
        //         .format("parquet")
        //         .option("checkpointLocation", "checkpoint_dir")
        //         .option("path", "parquet_path/")
        //         .toTable("myTable")
        //         .awaitTermination()
        //spark.read.table("myTable").show()

        val query0 = prunedDF.writeStream
                    .format("parquet") // can be "orc", "json", "csv", etc.
                    //.trigger(Trigger.ProcessingTime("10 seconds"))
                    .option("checkpointLocation", "checkpoint/")
                    .option("path", "parquet_path/")
                    .outputMode("append")
                    .start()
                    .awaitTermination()

        // val query2 = prunedDF.writeStream
        //         .outputMode("update")
        //         .format("console")        
        //         .start()
        
        // query2.awaitTermination()

        

        spark.stop()        
    }
}   