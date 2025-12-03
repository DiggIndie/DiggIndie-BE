package ceos.diggindie.domain.healthCheck;

import ceos.diggindie.common.code.SuccessCode;
import ceos.diggindie.common.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class healthCheckController {

    @GetMapping("/health-check")
    public ResponseEntity<Response<?>> healthCheckController() {

        Response<Void> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                null
        );

        return ResponseEntity.ok(response);
    }

}
