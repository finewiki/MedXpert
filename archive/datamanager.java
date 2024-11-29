import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class DatasetManager {
    private String datasetPath;
    private String datasetName;
    private String split;
    private String labelType;
    private String imageFormat;
    private Map<String, Object> datasetInfo;
    private List<String> imagePaths;
    private List<Integer> labels;
    private List<Integer> splitData;
    private ImageTransformer transformer;

    // Constructor
    public DatasetManager(String datasetPath, String datasetName, String split, String labelType, String imageFormat) {
        this.datasetPath = datasetPath;
        this.datasetName = datasetName;
        this.split = split;
        this.labelType = labelType;
        this.imageFormat = imageFormat;
        this.transformer = new ImageTransformer();
        
        // Initialize dataset metadata and paths
        this.datasetInfo = loadMetadata();
        this.imagePaths = getImagePaths();
        this.labels = loadLabels();
        this.splitData = getSplitData();
    }

    // Load metadata (dataset info)
    private Map<String, Object> loadMetadata() {
        String metadataPath = datasetPath + "/" + datasetName + "_metadata.json";
        try {
            String content = new String(Files.readAllBytes(Paths.get(metadataPath)));
            JSONObject json = new JSONObject(content);
            Map<String, Object> metadata = new HashMap<>();
            for (String key : json.keySet()) {
                metadata.put(key, json.get(key));
            }
            return metadata;
        } catch (IOException e) {
            throw new RuntimeException("Error loading metadata file.", e);
        }
    }

    // Get image file paths
    private List<String> getImagePaths() {
        String imagesDir = datasetPath + "/" + datasetName + "/images/";
        List<String> imageFiles = new ArrayList<>();
        File dir = new File(imagesDir);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(imageFormat)) {
                imageFiles.add(file.getPath());
            }
        }
        return imageFiles;
    }

    // Load labels from JSON file
    private List<Integer> loadLabels() {
        String labelsPath = datasetPath + "/" + datasetName + "/labels.json";
        try {
            String content = new String(Files.readAllBytes(Paths.get(labelsPath)));
            JSONArray jsonLabels = new JSONArray(content);
            List<Integer> labelList = new ArrayList<>();
            for (int i = 0; i < jsonLabels.length(); i++) {
                labelList.add(jsonLabels.getInt(i));
            }
            return labelList;
        } catch (IOException e) {
            throw new RuntimeException("Error loading labels file.", e);
        }
    }

    // Get split data (train/val/test)
    private List<Integer> getSplitData() {
        String splitFile = datasetPath + "/" + datasetName + "/" + split + "_split.json";
        try {
            String content = new String(Files.readAllBytes(Paths.get(splitFile)));
            JSONArray jsonSplit = new JSONArray(content);
            List<Integer> splitList = new ArrayList<>();
            for (int i = 0; i < jsonSplit.length(); i++) {
                splitList.add(jsonSplit.getInt(i));
            }
            return splitList;
        } catch (IOException e) {
            throw new RuntimeException("Error loading split file.", e);
        }
    }

    // Get a single item (image and label)
    public Map<String, Object> getItem(int index) {
        String imagePath = imagePaths.get(splitData.get(index));
        int label = labels.get(splitData.get(index));

        // Here we simply return a map with the file path and label
        Map<String, Object> item = new HashMap<>();
        item.put("image", imagePath); // Assuming we're just returning the image path as a placeholder
        item.put("label", label);
        return item;
    }

    // Get metadata for dataset
    public Map<String, Object> getMetadata() {
        return datasetInfo;
    }

    // Get the list of class names from the dataset info
    public List<String> getClassNames() {
        return (List<String>) datasetInfo.get("class_names");
    }

    // Get the number of samples in the dataset split
    public int size() {
        return splitData.size();
    }

    // Image transformation example
    class ImageTransformer {
        public String resize(String imagePath, int width, int height) {
            // Simulate image resizing (just returning the path for now)
            return imagePath + " (Resized to " + width + "x" + height + ")";
        }
    }

    public static void main(String[] args) {
        DatasetManager datasetManager = new DatasetManager(
            "path_to_dataset",
            "example_dataset",
            "train",
            "categorical",
            "jpg"
        );

        // Get a single item
        Map<String, Object> item = datasetManager.getItem(0);
        System.out.println(item);

        // Get metadata
        System.out.println(datasetManager.getMetadata());

        // Get class names
        System.out.println(datasetManager.getClassNames());
    }
}
