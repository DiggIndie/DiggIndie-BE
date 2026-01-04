package ceos.diggindie.common.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "성공"),
    _CREATED(HttpStatus.CREATED, "생성 성공"),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "삭제 성공");

    private final HttpStatus httpStatus;
    private final String message;
}