package de.familiep.mobileinformationgain.data_evaluation;

public interface ExportProgressCallback {
    void notifyProgress(long current, long max);
    void notifyFinished();
}
