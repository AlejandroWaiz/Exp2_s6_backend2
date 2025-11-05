package cl.duoc.ejemplo.microservicio.controllers;

import cl.duoc.ejemplo.microservicio.dto.ListS3ObjectDto;
import cl.duoc.ejemplo.microservicio.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final AwsS3Service awsS3Service;

    /** ✅ READ - Listar objetos en un bucket */
    @GetMapping("/{bucket}/objects")
    public ResponseEntity<List<ListS3ObjectDto>> listObjects(@PathVariable String bucket) {
        return ResponseEntity.ok(awsS3Service.listObjects(bucket));
    }

    /** ✅ READ - Descargar un objeto como byte[] */
    @GetMapping("/{bucket}/object")
    public ResponseEntity<byte[]> downloadObject(@PathVariable String bucket,@RequestParam String key) {
        byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    /** ✅ CREATE - Subir nuevo objeto */
    @PostMapping("/{bucket}/upload")
    public ResponseEntity<String> uploadObject(@PathVariable String bucket,@RequestParam("file") MultipartFile file,@RequestParam(required = false) String key) {
        try {
            String savedKey = awsS3Service.upload(bucket, key, file);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Archivo subido correctamente con key: " + savedKey);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al subir el archivo: " + e.getMessage());
        }
    }

    /** ✅ UPDATE - Sobrescribir objeto existente */
    @PutMapping("/{bucket}/object")
    public ResponseEntity<String> updateObject(@PathVariable String bucket,@RequestParam String key,@RequestParam("file") MultipartFile file) {
        try {
            awsS3Service.upload(bucket, key, file); // mismo método upload, sobrescribe
            return ResponseEntity.ok("Objeto actualizado correctamente: " + key);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    /** ✅ MOVE - Mover objeto dentro del bucket */
    @PostMapping("/{bucket}/move")
    public ResponseEntity<String> moveObject(@PathVariable String bucket,@RequestParam String sourceKey,@RequestParam String destKey) {
        awsS3Service.moveObject(bucket, sourceKey, destKey);
        return ResponseEntity.ok("Objeto movido de " + sourceKey + " a " + destKey);
    }

    /** ✅ DELETE - Eliminar objeto */
    @DeleteMapping("/{bucket}/object")
    public ResponseEntity<String> deleteObject(@PathVariable String bucket,@RequestParam String key) {
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.ok("Objeto eliminado correctamente: " + key);
    }
}
