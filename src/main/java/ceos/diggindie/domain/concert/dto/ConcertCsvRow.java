package ceos.diggindie.domain.concert.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertCsvRow {

    @CsvBindByName(column = "id")
    private Long externalId;

    @CsvBindByName(column = "documentId")
    private String documentId;

    @CsvBindByName(column = "title")
    private String title;

    @CsvBindByName(column = "slug")
    private String slug;

    @CsvBindByName(column = "startDate")
    private String startDate;

    @CsvBindByName(column = "endDate")
    private String endDate;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "preorderPrice")
    private String preorderPrice;

    @CsvBindByName(column = "onsitePrice")
    private String onsitePrice;

    @CsvBindByName(column = "performers")
    private String performers;

    @CsvBindByName(column = "venue")
    private String venue;

    @CsvBindByName(column = "address")
    private String address;

    @CsvBindByName(column = "ticketLink")
    private String ticketLink;

    @CsvBindByName(column = "posterUrl")
    private String posterUrl;

    @CsvBindByName(column = "url")
    private String url;
}