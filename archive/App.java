import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class DatasetManagerApp {
    
    public static void main(String[] args) {
        
        String datasetPath = "path_to_dataset";
        String datasetName = "example_dataset";
        String split = "train";  
        String labelType = "categorical"; 
        String imageFormat = "jpg"; 

        
        DatasetManager datasetManager = new DatasetManager(datasetPath, datasetName, split, labelType, imageFormat);

        displayMetadata(datasetManager);

        
        processDataset(datasetManager);

        
        transformImage(datasetManager);

        
        System.out.println("Dataset işleme tamamlandı.");
    }


    public static void displayMetadata(DatasetManager datasetManager) {
        System.out.println("Dataset Adı: " + datasetManager.getMetadata().get("name"));
        System.out.println("Class Names: " + datasetManager.getClassNames());
        System.out.println("Image Size: " + datasetManager.getMetadata().get("image_size"));
        System.out.println("Color Mode: " + datasetManager.getMetadata().get("color_mode"));
    }

    
    public static void processDataset(DatasetManager datasetManager) {
        for (int i = 0; i < datasetManager.size(); i++) {
            // Örnek öğeyi al
            Map<String, Object> item = datasetManager.getItem(i);
            String imagePath = (String) item.get("image");
            int label = (int) item.get("label");

            // Örnek bilgilerini ekrana yazdıralım
            System.out.println("Örnek " + (i + 1) + ": " + imagePath);
            System.out.println("Etiket: " + datasetManager.getClassNames().get(label));
        }
    }

   
    public static void transformImage(DatasetManager datasetManager) {
       
        String imagePath = datasetManager.getItem(0).get("image").toString();
        
        
        String resizedImage = datasetManager.transformer.resize(imagePath, 224, 224);
        
        
        System.out.println("Dönüştürülmüş Görsel: " + resizedImage);
    }
}
