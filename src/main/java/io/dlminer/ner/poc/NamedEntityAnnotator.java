package io.dlminer.ner.poc;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by slava on 23/08/17.
 */
public class NamedEntityAnnotator {

    private static final Logger log = Logger.getLogger(String.valueOf(NamedEntityAnnotator.class));


    private String modelPath;

    private TagConfig tagConfig;

    private TokenizerME tokenizer;

    private SentenceDetectorME sentenceDetector;

    private NameFinderME personFinder;

    private NameFinderME orgFinder;

    private NameFinderME locationFinder;

    private NameFinderME moneyFinder;

    private NameFinderME percentFinder;

    private NameFinderME timeFinder;


    private List<String> tokens;

    private List<String> tags;





    public NamedEntityAnnotator(String modelPath, TagConfig tagConfig) throws IOException {
        this.modelPath = modelPath;
        this.tagConfig = tagConfig;
        loadModels();
        tokens = new ArrayList<>();
        tags = new ArrayList<>();
    }

    private void loadModels() throws IOException {
        loadTokenizer();
        loadSentenceDetector();
        if (tagConfig.getPersonTag() != null) {
            loadPersonModel();
        }
        if (tagConfig.getOrgTag() != null) {
            loadOrgModel();
        }
        if (tagConfig.getLocationTag() != null) {
            loadLocationModel();
        }
        if (tagConfig.getTimeTag() != null) {
            loadTimeModel();
        }
        if (tagConfig.getMoneyTag() != null) {
            loadMoneyModel();
        }
        if (tagConfig.getPercentTag() != null) {
            loadPercentModel();
        }
    }




    private void loadTokenizer() throws IOException {
        File tokenModelFile = new File(modelPath + "en-token.bin");
        if (!tokenModelFile.exists()) {
            log.info("Tokenization cannot be used as the model is not found!");
            return;
        }
        InputStream tokenModelIS = new FileInputStream(tokenModelFile);
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIS);
        tokenizer = new TokenizerME(tokenModel);
    }

    private void loadSentenceDetector() throws IOException {
        File sentenceModelFile = new File(modelPath + "en-sent.bin");
        if (!sentenceModelFile.exists()) {
            log.info("Sentence detection cannot be used as the model is not found!");
            return;
        }
        InputStream sentenceModelIS = new FileInputStream(sentenceModelFile);
        SentenceModel sentenceModel = new SentenceModel(sentenceModelIS);
        sentenceDetector = new SentenceDetectorME(sentenceModel);
    }


    private void loadPersonModel() throws IOException {
        InputStream personModelIS = new FileInputStream(modelPath + "en-ner-person-rm.bin");
        TokenNameFinderModel personModel = new TokenNameFinderModel(personModelIS);
        personFinder = new NameFinderME(personModel);
    }

    private void loadOrgModel() throws IOException {
        InputStream orgModelIS = new FileInputStream(modelPath + "en-ner-organization-rm.bin");
        TokenNameFinderModel orgModel = new TokenNameFinderModel(orgModelIS);
        orgFinder = new NameFinderME(orgModel);
    }

    private void loadLocationModel() throws IOException {
        InputStream locationModelIS = new FileInputStream(modelPath + "en-ner-location-rm.bin");
        TokenNameFinderModel locationModel = new TokenNameFinderModel(locationModelIS);
        locationFinder = new NameFinderME(locationModel);
    }


    private void loadMoneyModel() throws IOException {
        InputStream moneyModelIS = new FileInputStream(modelPath + "en-ner-money-rm.bin");
        TokenNameFinderModel moneyModel = new TokenNameFinderModel(moneyModelIS);
        moneyFinder = new NameFinderME(moneyModel);
    }

    private void loadPercentModel() throws IOException {
        InputStream percentModelIS = new FileInputStream(modelPath + "en-ner-percentage-rm.bin");
        TokenNameFinderModel percentModel = new TokenNameFinderModel(percentModelIS);
        percentFinder = new NameFinderME(percentModel);
    }

    private void loadTimeModel() throws IOException {
        InputStream timeModelIS = new FileInputStream(modelPath + "en-ner-time-rm.bin");
        TokenNameFinderModel timeModel = new TokenNameFinderModel(timeModelIS);
        timeFinder = new NameFinderME(timeModel);
    }




    public void extract(String text) {
        if (tokenizer != null) {
            extract(tokenizer.tokenize(text));
        } else {
            throw new NullPointerException("Tokenizer is not initialized!");
        }
    }

    public void extract(String[] tokens) {
        String[] tags = new String[tokens.length];
        Arrays.fill(tags, tagConfig.getNotNamedTag());

        if (personFinder != null) {
            annotatePersons(tokens, tags);
        }
        if (orgFinder != null) {
            annotateOrganizations(tokens, tags);
        }
        if (locationFinder != null) {
            annotateLocations(tokens, tags);
        }
        if (timeFinder != null) {
            annotateTimes(tokens, tags);
        }
        if (moneyFinder != null) {
            annotateMoney(tokens, tags);
        }
        if (percentFinder != null) {
            annotatePercentage(tokens, tags);
        }

        this.tokens.addAll(Arrays.asList(tokens));
        this.tags.addAll(Arrays.asList(tags));
    }


    private void annotatePercentage(String[] tokens, String[] tags) {
        Span[] spans = percentFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getPercentTag();
            }
        }
    }

    private void annotateMoney(String[] tokens, String[] tags) {
        Span[] spans = moneyFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getMoneyTag();
            }
        }
    }

    private void annotateTimes(String[] tokens, String[] tags) {
        Span[] spans = timeFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getTimeTag();
            }
        }
    }


    private void annotateLocations(String[] tokens, String[] tags) {
        Span[] spans = locationFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getLocationTag();
            }
        }
    }

    private void annotateOrganizations(String[] tokens, String[] tags) {
        Span[] spans = orgFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getOrgTag();
            }
        }
    }


    private void annotatePersons(String[] tokens, String[] tags) {
        Span[] spans = personFinder.find(tokens);
        for (Span span : spans) {
            for (int i=span.getStart(); i<span.getEnd(); i++) {
                tags[i] = tagConfig.getPersonTag();
            }
        }
    }


    private AccuracyBean evaluate(String[] trueTags) {
        String[] tags = new String[this.tags.size()];
        this.tags.toArray(tags);
        NamedEntityEvaluator evaluator = new NamedEntityEvaluator(tags, trueTags, tagConfig);
        return evaluator.evaluate();
    }




    public static void main(String[] args) throws Exception {
        testKaggleDataset();
    }


    public static void testParagraph() throws IOException {
        String text = "Schank was a leading pioneer of artificial intelligence and cognitive psychology in the 1970s and 1980s. His innovations in these fields were conceptual dependency theory and case-based reasoning, both of which challenged cognitivist views of memory and reasoning.\n" +
                "\n" +
                "In 1969 Schank introduced the conceptual dependency theory for natural language understanding.[11] This model, partly based on the work of Sydney Lamb, was extensively used by Schank's students at Yale University, such as Robert Wilensky, Wendy Lehnert, and Janet Kolodner.\n" +
                "\n" +
                "Case-based reasoning (CBR) is based on Schank's model of dynamic memory[12] and was the basis for the earliest CBR systems: Janet Kolodner's CYRUS[13] and Michael Lebowitz's IPP.[14]\n" +
                "\n" +
                "Other schools of CBR and closely allied fields emerged in the 1980s, investigating such topics as CBR in legal reasoning, memory-based reasoning (a way of reasoning from examples on massively parallel machines), and combinations of CBR with other reasoning methods. In the 1990s, interest in CBR grew, as evidenced by the establishment of an International Conference on Case-Based Reasoning in 1995, as well as European, German, British, Italian, and other CBR workshops.\n" +
                "\n" +
                "CBR technology has produced a number of successful deployed systems, the earliest being Lockheed's CLAVIER,[15] a system for laying out composite parts to be baked in an industrial convection oven. CBR has been used extensively in help desk applications such as the Compaq SMART system[16] and has found a major application area in the health sciences.[17]";

        TagConfig tagConfig = new TagConfig();
        tagConfig.setNotNamedTag("O");
        tagConfig.setPersonTag("per");
        tagConfig.setOrgTag("org");
        tagConfig.setLocationTag("geo");


        final String modelPath = System.getProperty("user.dir") + "/open-nlp/src/main/resources/models/";
        NamedEntityAnnotator annotator = new NamedEntityAnnotator(modelPath, tagConfig);
        annotator.extract(text);
    }


    public static void testKaggleDataset() throws IOException {
        final String modelPath = System.getProperty("user.dir") + "/open-nlp/src/main/resources/models/";
        final String inputCSV = System.getProperty("user.dir") + "/open-nlp/src/main/resources/data/ner_dataset.csv";

        TagConfig tagConfig = new TagConfig();
        tagConfig.setNotNamedTag("O");
        tagConfig.setPersonTag("per");
        tagConfig.setOrgTag("org");
        tagConfig.setLocationTag("geo");
        tagConfig.setTimeTag("tim");

        log.info("Initializing the extractor");
        NamedEntityAnnotator extractor = new NamedEntityAnnotator(modelPath, tagConfig);

        log.info("Reading the file");
        com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(inputCSV));
        List<String[]> rows = reader.readAll();
        rows.remove(0);

        log.info("Extracting named entities");
        int maxTokens = rows.size();
        ArrayList<String> tokens = new ArrayList<>();
        for (int i=0; i<maxTokens; i++) {
            tokens.add(rows.get(i)[1]);
            if ((!rows.get(i)[0].isEmpty() && i != 0) || i >= maxTokens-1) {
                String[] tokenArray = new String[tokens.size()];
                tokens.toArray(tokenArray);
                extractor.extract(tokenArray);
                tokens.clear();
            }
            if (i % 1000 == 0) {
                log.info("\t" + i + " / " + maxTokens + " tokens are processed");
            }
        }


        // check accuracy
        log.info("Checking accuracy");
        String[] trueTags = new String[maxTokens];
        for (int i=0; i<maxTokens; i++) {
            String tag = rows.get(i)[3];
            trueTags[i] = tagConfig.match(tag);
        }

        AccuracyBean metrics = extractor.evaluate(trueTags);
        log.info(metrics.toString());
    }




}
