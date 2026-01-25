package ceos.diggindie.domain.concert.dto;

import java.util.List;

public record CsvImportResult(
        int totalRows,
        int successCount,
        int skippedCount,
        int failedCount,
        List<String> errors
) {
    public static CsvImportResult of(int total, int success, int skipped, List<String> errors) {
        return new CsvImportResult(total, success, skipped, errors.size(), errors);
    }
}
