import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming._
import org.apache.spark.sql.types._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json

object CalculateData{
    def main(args: Array[String]){
        
        val spark = SparkSession
          .builder
          .master("local[*]")
          .appName("CalculateData")
          .getOrCreate()
        
        import spark.implicits._ //help us avoid some warnings?
        
        val table = spark.read.parquet("parquet_path/")
        """
        Reference sources:

        //https://stackoverflow.com/questions/42003609/spark-count-and-filtered-count-in-same-query
        //https://stackoverflow.com/questions/40449139/how-to-calculate-sum-and-count-in-a-single-groupby
        //https://stackoverflow.com/questions/29988287/renaming-columns-for-pyspark-dataframe-aggregates

        //client.groupBy("Categ").agg(sum("Amnt"),count("ID")).show()
        //df.select(count($"id"), count(when($"column1" === 1, true)))
        """

        //Question1:
        val affinity = table.groupBy("id").agg((count(when($"supplyTag_tags"===1, true))/count("supplyTag_tags")).alias("Shopping"), 
                                              (count(when($"supplyTag_tags"===2, true))/count("supplyTag_tags")).alias("Programme_tv"),
                                              (count(when($"supplyTag_tags"===3, true))/count("supplyTag_tags")).alias("Sport"),
                                              (count(when($"supplyTag_tags"===4, true))/count("supplyTag_tags")).alias("News"),
                                              (count(when($"supplyTag_tags"===5, true))/count("supplyTag_tags")).alias("Sante"),
                                              (count(when($"supplyTag_tags"===6, true))/count("supplyTag_tags")).alias("Voyage"),
        )

        affinity.show()

        //Question2:
        // val ssp_devices = table.groupBy("deviceOs").agg(collect_list(struct("rank", "ssp")))

        // ssp_devices.show()

        //Question3:
        table.createOrReplaceTempView("DATA")
        
        //val top1domain = spark.sql("SELECT c, supplyTag_domain, COUNT(supplyTag_domain) as cs FROM DATA GROUP BY c ORDER BY cs limit 1")
        val top1domain = spark.sql("WITH cte AS ( SELECT c, supplyTag_domain, ROW_NUMBER() OVER (PARTITION BY c ORDER BY COUNT(supplyTag_domain) DESC) rn FROM DATA GROUP BY c, supplyTag_domain) SELECT c, supplyTag_domain FROM cte WHERE rn = 1")
        top1domain.show()



        //Another way of loading the parquet data into stream:
        
        // val userSchema: StructType = new StructType()
        //     .add("ssp", StringType)
        //     .add("id", StringType)
        //     .add("deviceOs", StringType)
        //     .add("c", StringType)
        //     .add("supplyType", StringType)
        //     .add("supplyTag_tags", IntegerType)
        //     .add("supplyTag_domain", StringType)
        //     .add("consentMetadata_userConsent", BooleanType)
        //     .add("bidderVersion", StringType)
        //     .add("ts_no_bid", LongType)
        //     .add("date", DateType)
        //     .add("y", StringType)
        //     .add("m", StringType)
        //     .add("d", StringType)
        //     .add("h", StringType)
        
        // spark.read.table("myTable").show()

        // val df = spark.readStream
        //     .schema(userSchema)//Below codes provides example
        //     .parquet("parquet_path/")

        // val query2 = df.writeStream
        //         .outputMode("update")
        //         .format("console")        
        //         .start()
        //         .awaitTermination()
        

        spark.stop()   
    }
}