package io.dlminer.ner.poc;

import com.opencsv.CSVReader;
import io.dlminer.ner.utils.Validate;

import java.io.*;
import java.util.List;
import java.util.logging.Logger;

import static io.dlminer.ner.poc.NamedEntityType.UNNAMED;
import static io.dlminer.ner.poc.TagConfig.getEndTag;
import static io.dlminer.ner.poc.TagConfig.getStartTag;

/**
 * Created by slava on 03/09/17.
 */
public class DataSetProcessor {

    private static final Logger log = Logger.getLogger(String.valueOf(DataSetProcessor.class));


    private TagConfig tagConfig;
    private File inputCSV;
    private File outputDir;

    private List<String[]> csvRows;

    public DataSetProcessor(File inputCSV, File outputDir, TagConfig tagConfig) {
        Validate.notNull(inputCSV);
        Validate.notNull(outputDir);
        Validate.notNull(tagConfig);

        this.inputCSV = inputCSV;
        this.outputDir = outputDir;
        this.tagConfig = tagConfig;
    }

    // need to check the format
    private void read() throws IOException {
        CSVReader reader = new CSVReader(new FileReader(inputCSV));
        csvRows = reader.readAll();
        csvRows.remove(0);
    }

    private void convert() {
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        for (NamedEntityType type : NamedEntityType.values()) {
            if (tagConfig.get(type) == null || type.equals(UNNAMED)) {
                continue;
            }

            log.info("Converting to the OpenNLP format: " + type.getType());
            File outFile = new File(outputDir.getPath() + "/en-ner-" + type.getType() + "-rm.train");
            convertTo(outFile, type);
        }
    }

    private void convertTo(File outFile, NamedEntityType type) {
        try {
            PrintWriter writer = new PrintWriter(outFile, "UTF-8");
            int maxTokens = csvRows.size();
            String sentence = "";
            boolean isStartTagOpen = false;

            for (int i = 0; i < maxTokens; i++) {
                String token = csvRows.get(i)[1];
                String tag = csvRows.get(i)[3];
                NamedEntityType tagType = tagConfig.getTypeOf(tagConfig.match(tag));
                String addedTag = "";
                if (type.equals(tagType)) {
                    if (!isStartTagOpen) {
                        addedTag = getStartTag(type) + " ";
                        isStartTagOpen = true;
                    }
                } else {
                    if (isStartTagOpen) {
                        addedTag = getEndTag() + " ";
                        isStartTagOpen = false;
                    }
                }
                sentence += addedTag + token + " ";
                if (isSentenceEnd(i + 1, maxTokens)) {
                    if (isStartTagOpen) {
                        sentence += getEndTag() + " ";
                        isStartTagOpen = false;
                    }
                    writer.println(sentence);
                    sentence = "";
                }
                // debug
                if (i % 1000 == 0) {
                    log.info("\t" + i + " / " + maxTokens + " tokens are processed");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isSentenceEnd(int i, int maxTokens) {
        return i >= maxTokens - 1 || (!csvRows.get(i)[0].isEmpty() && i != 0);
    }


    public static void main(String[] args) throws IOException {
        convertKaggleDataset();
    }


    public static void convertKaggleDataset() throws IOException {
        File csvFile = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/data/ner_dataset.csv");
        File outDir = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/train/");
        if (!csvFile.exists()) {
            throw new FileNotFoundException("Please specify the correct input data file!");
        }
        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new IOException("The directory for train data is not created!");
            }
        }

        TagConfig tagConfig = new TagConfig();
        tagConfig.setNotNamedTag("O");
        tagConfig.setPersonTag("per");
        tagConfig.setOrgTag("org");
        tagConfig.setLocationTag("geo");
        tagConfig.setTimeTag("tim");

        log.info("Reading the file");
        DataSetProcessor processor = new DataSetProcessor(csvFile, outDir, tagConfig);
        processor.read();
        processor.convert();
    }

}
