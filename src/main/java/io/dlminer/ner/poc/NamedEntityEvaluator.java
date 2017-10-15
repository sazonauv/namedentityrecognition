package io.dlminer.ner.poc;

/**
 * Created by slava on 27/08/17.
 */
public class NamedEntityEvaluator {


    private String[] tags;

    private String[] trueTags;

    private TagConfig tagConfig;


    private AccuracyBean metrics;


    public NamedEntityEvaluator(String[] tags, String[] trueTags,
                                TagConfig tagConfig) {
        this.tags = tags;
        this.trueTags = trueTags;
        this.tagConfig = tagConfig;
    }


    public AccuracyBean evaluate() {
        metrics = new AccuracyBean();

        double[] rpo = evaluateOverall();
        metrics.setOverallRecall(rpo[0]);
        metrics.setOverallPrecision(rpo[1]);

        if (tagConfig.getPersonTag() != null) {
            double[] rp = evaluateTag(tagConfig.getPersonTag());
            metrics.setPersonRecall(rp[0]);
            metrics.setPersonPrecision(rp[1]);
        }

        if (tagConfig.getOrgTag() != null) {
            double[] rp = evaluateTag(tagConfig.getOrgTag());
            metrics.setOrgRecall(rp[0]);
            metrics.setOrgPrecision(rp[1]);
        }

        if (tagConfig.getLocationTag() != null) {
            double[] rp = evaluateTag(tagConfig.getLocationTag());
            metrics.setLocationRecall(rp[0]);
            metrics.setLocationPrecision(rp[1]);
        }

        if (tagConfig.getTimeTag() != null) {
            double[] rp = evaluateTag(tagConfig.getTimeTag());
            metrics.setTimeRecall(rp[0]);
            metrics.setTimePrecision(rp[1]);
        }

        if (tagConfig.getMoneyTag() != null) {
            double[] rp = evaluateTag(tagConfig.getMoneyTag());
            metrics.setMoneyRecall(rp[0]);
            metrics.setMoneyPrecision(rp[1]);
        }

        if (tagConfig.getPercentTag() != null) {
            double[] rp = evaluateTag(tagConfig.getPercentTag());
            metrics.setPercentRecall(rp[0]);
            metrics.setPercentPrecision(rp[1]);
        }

        return metrics;
    }


    private double[] evaluateOverall() {
        String nnTag = tagConfig.getNotNamedTag();

        double positives = 0;
        for (int i=0; i<trueTags.length; i++) {
            if (!trueTags[i].equals(nnTag)) {
                positives++;
            }
        }

        double truePositives = 0;
        double falsePositives = 0;
        for (int i=0; i<trueTags.length; i++) {
            if (!trueTags[i].equals(nnTag) && !tags[i].equals(nnTag)) {
                truePositives++;
            }
            if (trueTags[i].equals(nnTag) && !tags[i].equals(nnTag)) {
                falsePositives++;
            }
        }

        double recall = truePositives / positives;
        double precision = truePositives / (truePositives + falsePositives);

        return new double[]{recall, precision};
    }


    private double[] evaluateTag(String tag) {

        double positives = 0;
        for (int i=0; i<trueTags.length; i++) {
            if (trueTags[i].equals(tag)) {
                positives++;
            }
        }

        double truePositives = 0;
        double falsePositives = 0;
        for (int i=0; i<trueTags.length; i++) {
            if (trueTags[i].equals(tag) && tags[i].equals(tag)) {
                truePositives++;
            }
            if (!trueTags[i].equals(tag) && tags[i].equals(tag)) {
                falsePositives++;
            }
        }

        double recall = truePositives / positives;
        double precision = truePositives / (truePositives + falsePositives);

        return new double[]{recall, precision};
    }


}
