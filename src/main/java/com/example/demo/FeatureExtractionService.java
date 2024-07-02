package com.example.demo;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FeatureExtractionService {

    private SparkSession spark;

    public FeatureExtractionService(SparkSession spark) {
        this.spark = spark;
    }

    public Dataset<Row> extractFeatures(List<String> messages) {
        Dataset<Row> messageDF = spark.createDataFrame(messages, String.class).toDF("message");

        Tokenizer tokenizer = new Tokenizer().setInputCol("message").setOutputCol("words");
        Dataset<Row> wordsData = tokenizer.transform(messageDF);

        HashingTF hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(1000);

        Dataset<Row> featurizedData = hashingTF.transform(wordsData);

        IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        IDFModel idfModel = idf.fit(featurizedData);

        return idfModel.transform(featurizedData);
    }
}
