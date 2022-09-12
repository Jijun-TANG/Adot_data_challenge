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

        val userSchema: StructType = new StructType()
            .add("ssp", StringType)
            .add("id", StringType)
            .add("deviceOs", StringType)
            .add("c", StringType)
            .add("supplyType", StringType)
            .add("supplyTag_tags", IntegerType)
            .add("supplyTag_domain", StringType)
            .add("consentMetadata_userConsent", BooleanType)
            .add("bidderVersion", StringType)
            .add("ts_no_bid", LongType)
            .add("date", DateType)
            .add("y", StringType)
            .add("m", StringType)
            .add("d", StringType)
            .add("h", StringType)

        val df = spark.readStream
            .schema(userSchema)//Below codes provides example
            .parquet("parquet_path/")

        val query2 = df.writeStream
                .outputMode("update")
                .format("console")        
                .start()
                .awaitTermination()
        

        spark.stop()   
    }
}