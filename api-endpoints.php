<?php
header('Content-Type: application/json');


if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['patient_id'])) {
    $patient_id = $_GET['patient_id'];

    // Veritabanı bağlantısı
    $conn = new mysqli('localhost', 'root', '', 'medxpert');
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $sql = "SELECT * FROM health_data WHERE patient_id = '$patient_id'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $data = $result->fetch_assoc();
        echo json_encode($data);
    } else {
        echo json_encode(['error' => 'Veri bulunamadı']);
    }
}
?>
