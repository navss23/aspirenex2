package com.example.demo;

import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.sql.*;

import org.springframework.stereotype.Service;

@Service
public class ModelTrainingService {

    private LogisticRegressionModel model;

    public void trainModel(Dataset<Row> trainingData) {
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.3)
                .setElasticNetParam(0.8);

        model = lr.fit(trainingData);
    }

    public Dataset<Row> predict(Dataset<Row> testData) {
        return model.transform(testData);
    }
}

