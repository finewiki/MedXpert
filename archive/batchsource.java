import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.logging.Logger;

public class MultiMedIMetaBatchTaskSource {
    private static final Logger logger = Logger.getLogger(MultiMedIMetaBatchTaskSource.class.getName());
    private List<TaskSource> batchedTaskSources;

    public MultiMedIMetaBatchTaskSource(
            String medimetaDataPath,
            List<Pair<String, String>> taskIds,
            int batchSize,
            boolean shuffle,
            boolean dropLast,
            Function<Object, Object> collateFn,
            Object splits,
            Object originalSplits,
            Function<Object, Object> transform) {

        logger.info(String.format("Initializing MultiMedIMetaBatchTaskSource with medimetaDataPath=%s, taskIds=%s, batchSize=%d, shuffle=%b, dropLast=%b, collateFn=%s, originalSplits=%s, transform=%s",
                medimetaDataPath, taskIds, batchSize, shuffle, dropLast, collateFn, originalSplits, transform));

        List<String> splitList = prepareSplits(splits, taskIds.size());
        List<String> originalSplitList = prepareSplits(originalSplits, taskIds.size());

        List<MedIMetaTaskSource> unbatchedTaskSources = createUnbatchedTaskSources(medimetaDataPath, taskIds, splitList, originalSplitList, transform);
        this.batchedTaskSources = createBatchedTaskSources(unbatchedTaskSources, batchSize, shuffle, dropLast, collateFn);

    }

    private List<String> prepareSplits(Object splits, int size) {
        List<String> splitList = new ArrayList<>();
        if (splits == null) {
            return splitList;
        } else if (splits instanceof String) {
            for (int i = 0; i < size; i++) {
                splitList.add((String) splits);
            }
        } else if (splits instanceof List) {
            splitList = (List<String>) splits;
        }
        return splitList;
    }

    private List<MedIMetaTaskSource> createUnbatchedTaskSources(
            String dataPath,
            List<Pair<String, String>> taskIds,
            List<String> splits,
            List<String> originalSplits,
            Function<Object, Object> transform) {

        List<MedIMetaTaskSource> taskSources = new ArrayList<>();
        for (int i = 0; i < taskIds.size(); i++) {
            Pair<String, String> taskId = taskIds.get(i);
            MedIMetaTaskSource taskSource = new MedIMetaTaskSource(
                    dataPath,
                    taskId.getFirst(),
                    taskId.getSecond(),
                    splits.get(i),
                    originalSplits.get(i),
                    transform
            );
            taskSources.add(taskSource);
        }
        return taskSources;
    }

    private List<TaskSource> createBatchedTaskSources(
            List<MedIMetaTaskSource> unbatchedTaskSources,
            int batchSize,
            boolean shuffle,
            boolean dropLast,
            Function<Object, Object> collateFn) {

        List<TaskSource> batchedTaskSources = new ArrayList<>();
        for (MedIMetaTaskSource taskSource : unbatchedTaskSources) {
            BatchedTaskSource batchedTaskSource = new BatchedTaskSource(
                    taskSource,
                    batchSize,
                    shuffle,
                    dropLast,
                    collateFn
            );
            batchedTaskSources.add(batchedTaskSource);
        }
        return batchedTaskSources;
    }

    public List<TaskSource> getBatchedTaskSources() {
        return batchedTaskSources;
    }

    private class MedIMetaTaskSource {
        private String dataPath;
        private String datasetId;
        private String taskName;
        private String split;
        private String originalSplit;
        private Function<Object, Object> transform;

        public MedIMetaTaskSource(String dataPath, String datasetId, String taskName, String split, String originalSplit, Function<Object, Object> transform) {
            this.dataPath = dataPath;
            this.datasetId = datasetId;
            this.taskName = taskName;
            this.split = split;
            this.originalSplit = originalSplit;
            this.transform = transform;
        }
    }

    private class BatchedTaskSource {
        private MedIMetaTaskSource taskSource;
        private int batchSize;
        private boolean shuffle;
        private boolean dropLast;
        private Function<Object, Object> collateFn;

        public BatchedTaskSource(MedIMetaTaskSource taskSource, int batchSize, boolean shuffle, boolean dropLast, Function<Object, Object> collateFn) {
            this.taskSource = taskSource;
            this.batchSize = batchSize;
            this.shuffle = shuffle;
            this.dropLast = dropLast;
            this.collateFn = collateFn;
        }
    }

    private static class Pair<A, B> {
        private A first;
        private B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }
}
