package com.example.demo;

import java.io.IOException;
import java.util.Collections;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spam")
public class SMSController {

    @Autowired
    private SMSDatasetService datasetService;

    @Autowired
    private FeatureExtractionService featureService;

    @Autowired
    private ModelTrainingService trainingService;

    @Autowired
    private ModelEvaluationService evaluationService;

    @PostMapping("/train")
    public String trainModel() throws IOException {
        datasetService.loadDataset("src/main/resources/SMSSpamCollection.csv");
        Dataset<Row> features = featureService.extractFeatures(datasetService.getMessages());

        // Split data into training and test sets
        Dataset<Row>[] splits = features.randomSplit(new double[]{0.8, 0.2}, 1234L);
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testData = splits[1];

        // Train the classifier
        trainingService.trainModel(trainingData);

        // Make predictions and evaluate
        Dataset<Row> predictions = trainingService.predict(testData);
        evaluationService.evaluate(predictions);

        return "Model trained and evaluated.";
    }

    @PostMapping("/predict")
    public String predictSpam(@RequestBody String message) {
        // Extract features for the new message
        Dataset<Row> features = featureService.extractFeatures(Collections.singletonList(message));

        // Predict using the trained model
        Dataset<Row> prediction = trainingService.predict(features);

        // Return prediction result
        Row row = prediction.select("prediction").first();
        double predictionResult = row.getDouble(0);
        return predictionResult == 1.0 ? "Spam" : "Not Spam";
    }
}
