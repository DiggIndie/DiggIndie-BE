package ceos.diggindie.domain.concert.service;

import ceos.diggindie.domain.concert.dto.ConcertCsvRow;
import ceos.diggindie.domain.concert.dto.CsvImportResult;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertCsvImportService {

    private final ConcertAppender concertAppender;

    public CsvImportResult importFromCsv(MultipartFile file) {
        List<ConcertCsvRow> rows = parseCsv(file);

        int success = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            ConcertCsvRow row = rows.get(i);
            try {
                boolean saved = concertAppender.appendConcert(row);

                if (saved) success++;
                else skipped++;

            } catch (Exception e) {
                String error = String.format(
                        "Row %d (%s): %s",
                        i + 2, row.getTitle(), e.getMessage()
                );
                errors.add(error);
                log.error("CSV Import Error [Row {}]", i + 2, e);
            }
        }

        log.info("CSV Import 완료 - 총: {}, 성공: {}, 스킵: {}, 실패: {}",
                rows.size(), success, skipped, errors.size());

        return CsvImportResult.of(rows.size(), success, skipped, errors);
    }

    private List<ConcertCsvRow> parseCsv(MultipartFile file) {
        try (Reader reader =
                     new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {

            return new CsvToBeanBuilder<ConcertCsvRow>(reader)
                    .withType(ConcertCsvRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build()
                    .parse();

        } catch (Exception e) {
            throw new RuntimeException("CSV 파싱 실패", e);
        }
    }
}
