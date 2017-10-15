package io.dlminer.ner.poc;

import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import opennlp.tools.util.eval.FMeasure;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * Created by slava on 31/08/17.
 */
public class NamedEntityModel {

    private static final Logger log = Logger.getLogger(String.valueOf(NamedEntityModel.class));

    private NamedEntityType type;

    private TokenNameFinderModel model;

    public NamedEntityModel(NamedEntityType type) {
        this.type = type;
    }


    public void train(File dataFile) throws IOException {
        ObjectStream<NameSample> sampleStream = getStream(dataFile);
        TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
        TrainingParameters parameters = TrainingParameters.defaultParams();
        parameters.put(TrainingParameters.CUTOFF_PARAM, 5);
        parameters.put(TrainingParameters.ITERATIONS_PARAM, 20);
        model = NameFinderME.train("en", type.getType(),
                sampleStream, parameters, nameFinderFactory);
        sampleStream.close();
    }

    public FMeasure test(File dataFile) throws IOException {
        ObjectStream<NameSample> sampleStream = getStream(dataFile);
        TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
        TrainingParameters parameters = TrainingParameters.defaultParams();
        parameters.put(TrainingParameters.CUTOFF_PARAM, 5);
        parameters.put(TrainingParameters.ITERATIONS_PARAM, 20);
        TokenNameFinderCrossValidator evaluator = new TokenNameFinderCrossValidator(
                "en", type.getType(), parameters, nameFinderFactory);
        evaluator.evaluate(sampleStream, 10);
        sampleStream.close();
        FMeasure result = evaluator.getFMeasure();
        return result;
    }

    private ObjectStream<NameSample> getStream(File dataFile) throws IOException {
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(dataFile);
        Charset charset = Charset.forName("UTF-8");
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, charset);
        ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
        return sampleStream;
    }

    public void save(File modelFile) throws IOException {
        OutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
            model.serialize(modelOut);
        } finally {
            if (modelOut != null)
                modelOut.close();
        }
    }

    public static void main(String[] args) throws IOException {
        File trainDir = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/train/");
        trainFrom(trainDir);
    }


    public static void trainFrom(File trainDir) throws IOException {
        if (!trainDir.exists()) {
            throw new FileNotFoundException("The directory with training data is not found!");
        }
        File modelDir = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/models/");

        if (!modelDir.exists()) {
            if (!modelDir.mkdirs()) {
                throw new IOException("The directory for models is not created!");
            }
        }

        for (File trainFile : trainDir.listFiles()) {
            NamedEntityType type = NamedEntityType.findIn(trainFile.getName());
            if (type == null) {
                continue;
            }
            log.info("Training a model for " + type.getType());
            NamedEntityModel model = new NamedEntityModel(type);
            model.train(trainFile);
            File modelFile = new File(modelDir, trainFile.getName().replace(".train", ".bin"));
            log.info("Saving to " + modelFile);
            model.save(modelFile);
        }
    }

}
