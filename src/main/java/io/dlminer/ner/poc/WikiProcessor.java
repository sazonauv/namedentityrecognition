package io.dlminer.ner.poc;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Created by slava on 16/09/17.
 */
public class WikiProcessor {

    private static Logger log = Logger.getLogger(WikiProcessor.class.toString());

    private SentenceDetectorME sentenceDetector;

    public WikiProcessor(File sentenceModelFile) throws IOException {
        loadModel(sentenceModelFile);
    }


    private void loadModel(File sentenceModelFile) throws IOException {
        if (!sentenceModelFile.exists()) {
            log.info("Sentence detection cannot be used as the model is not found!");
            return;
        }
        InputStream sentenceModelIS = new FileInputStream(sentenceModelFile);
        SentenceModel sentenceModel = new SentenceModel(sentenceModelIS);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
    }


    public static void main(String[] args) throws Exception {
        processWikipedia();
    }


    public static void processWikipedia() throws Exception {
        log.info("Reading file....");
        String path = System.getProperty("user.dir") + "/open-nlp/src/main/resources/";
        File inputFile = new File(path + "data/englishText_0_10000");
        File outputFile = new File(path + "out/" + inputFile.getName() + ".txt");
        File modelFile = new File(path + "models/en-sent.bin");

        WikiProcessor processor = new WikiProcessor(modelFile);
        processor.printSentences(inputFile);

    }

    private void printSentences(File inputFile) {

    }


}
