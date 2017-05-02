package de.familiep.mobileinformationgain.data_evaluation.behaviours;

import java.io.File;

import de.familiep.mobileinformationgain.data_evaluation.ExportProgressCallback;
import de.familiep.mobileinformationgain.io.FileHelper;
import de.familiep.mobileinformationgain.persistence2.DbReadAccessHelper;

public interface EvaluationBehavior {
    void generateAllData(DbReadAccessHelper dbAccessHelper, FileHelper fileHelper,
                         ExportProgressCallback callback);
}
