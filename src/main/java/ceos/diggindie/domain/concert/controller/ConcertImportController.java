package ceos.diggindie.domain.concert.controller;

import ceos.diggindie.domain.concert.dto.CsvImportResult;
import ceos.diggindie.domain.concert.service.ConcertCsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Concert Import", description = "공연 데이터 가져오기 (관리자)")
public class ConcertImportController {

    private final ConcertCsvImportService importService;

    @Operation(summary = "CSV 파일로 공연 일괄 등록 (관리자)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/api/admin/concerts/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CsvImportResult> importFromCsv(
            @RequestPart("file") MultipartFile file
    ) {
        log.info("CSV Import 요청: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CsvImportResult result = importService.importFromCsv(file);
        return ResponseEntity.ok(result);
    }
}