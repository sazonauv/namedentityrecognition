package io.dlminer.ner.poc;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.dlminer.ner.utils.ArrayIndexComparator;
import io.dlminer.ner.utils.Validate;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by slava on 06/09/17.
 */
public class EntityLinkPredictor {

    private static final Logger log = Logger.getLogger(String.valueOf(EntityLinkPredictor.class));

    private TagConfig tagConfig;
    private File inputCSV;
    private List<String[]> csvRows;

    private List<Set<String>> entityBuckets;
    private Map<String, Set<Integer>> coOccurenceMap;
    private List<String> entityList;

    private double[][] linkMatrix;



    public EntityLinkPredictor(File inputCSV, TagConfig tagConfig) {
        Validate.notNull(inputCSV);
        Validate.notNull(tagConfig);

        this.inputCSV = inputCSV;
        this.tagConfig = tagConfig;
    }


    public void read() throws IOException {
        CSVReader reader = new CSVReader(new FileReader(inputCSV));
        csvRows = reader.readAll();
        csvRows.remove(0);
        readEntities();
    }

    private void readEntities() {
        int maxTokens = csvRows.size();
        entityBuckets = new LinkedList<>();
        coOccurenceMap = new HashMap<>();
        String entity = "";
        Set<String> bucket = new HashSet<>();
        boolean isStartTagOpen = false;
        for (int i = 0; i < maxTokens; i++) {
            String token = csvRows.get(i)[1];
            String tag = csvRows.get(i)[3];
            if (!tagConfig.isUnnamed(tag)) {
                if (!isStartTagOpen) {
                    entity += token;
                    isStartTagOpen = true;
                }
            } else {
                if (isStartTagOpen) {
                    bucket.add(entity);
                    entity = "";
                    isStartTagOpen = false;
                }
            }
            if (isSentenceEnd(i + 1, maxTokens)) {
                if (isStartTagOpen) {
                    bucket.add(entity);
                    entity = "";
                    isStartTagOpen = false;
                }
                entityBuckets.add(bucket);
                int lastID = entityBuckets.size() - 1;
                for (String e : bucket) {
                    Set<Integer> occIDs = coOccurenceMap.get(e);
                    if (occIDs == null) {
                        occIDs = new HashSet<>();
                        coOccurenceMap.put(e, occIDs);
                    }
                    occIDs.add(lastID);
                }
                bucket = new HashSet<>();
            }
        }
        entityList = new ArrayList<>(coOccurenceMap.keySet());
        log.info(entityList.size() + " unique entities are collected");
        log.info(entityBuckets.size() + " entity buckets are collected");
    }


    private boolean isSentenceEnd(int i, int maxTokens) {
        return i >= maxTokens - 1 || (!csvRows.get(i)[0].isEmpty() && i != 0);
    }


    public void predictLinks() {
        linkMatrix = new double[entityList.size()][entityList.size()];
        for (int i=0; i<entityList.size()-1; i++) {
            Set<Integer> occIDs1 = coOccurenceMap.get(entityList.get(i));
            for (int j=i+1; j<entityList.size(); j++) {
                Set<Integer> occIDs2 = coOccurenceMap.get(entityList.get(j));
                int intersect = countIntersection(occIDs1, occIDs2);
                linkMatrix[i][j] = intersect/occIDs1.size();
                linkMatrix[j][i] = intersect/occIDs2.size();
            }
            linkMatrix[i][i] = 1;
        }
    }


    private int countIntersection(Set<?> set1, Set<?> set2) {
        if (set1 == null || set2 == null) {
            return 0;
        }
        int count = 0;
        if (set1.size() <= set2.size()) {
            for (Object o : set1) {
                if (set2.contains(o)) {
                    count++;
                }
            }
        } else {
            for (Object o : set2) {
                if (set1.contains(o)) {
                    count++;
                }
            }
        }
        return count;
    }


    public void saveLinks(File outputCSV) throws IOException {
        File outputDir = outputCSV.getParentFile();
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new IOException("The output directory for links cannot be created!");
            }
        }
        CSVWriter writer = new CSVWriter(new FileWriter(outputCSV));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        for (int i=0; i<linkMatrix.length; i++) {
            String[] row = new String[linkMatrix[0].length];
            for (int j=0; j<linkMatrix[0].length; j++) {
                row[j] = df.format(linkMatrix[i][j]);
            }
            writer.writeNext(row);
        }
        writer.close();
    }


    private void printStrongestLinks(int number) {
        int n = linkMatrix.length;
        for (int i=0; i<n; i++) {
            ArrayIndexComparator comparator = new ArrayIndexComparator(linkMatrix[i], false);
            Integer[] indices = comparator.createIndexArray();
            Arrays.sort(indices, comparator);
            String linkStr = entityList.get(i) + " -> ";
            for (int k=0; k<number; k++) {
                linkStr += entityList.get(indices[k]) + ", ";
            }
            System.out.println(linkStr);
        }
    }



    public static void main(String[] args) throws IOException {
        testKaggleDataset();
    }

    private static void testKaggleDataset() throws IOException {
        File inputCSV = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/data/ner_dataset.csv");
        if (!inputCSV.exists()) {
            throw new FileNotFoundException("Please specify the correct input data file!");
        }

        TagConfig tagConfig = new TagConfig();
        tagConfig.setNotNamedTag("O");
        tagConfig.setPersonTag("per");
        tagConfig.setOrgTag("org");
        tagConfig.setLocationTag("geo");

        log.info("Reading the file");
        EntityLinkPredictor predictor = new EntityLinkPredictor(inputCSV, tagConfig);
        predictor.read();
        log.info("Predicting links between entities");
        predictor.predictLinks();
        log.info("Printing links between entities");
        predictor.printStrongestLinks(10);

//        File outputCSV = new File(System.getProperty("user.dir") + "/open-nlp/src/main/resources/data/links.csv");
//        predictor.saveLinks(outputCSV);

    }




}
