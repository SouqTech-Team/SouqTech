package org.stand.springbootecommerce.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErrorBaseResponseBody extends BaseResponseBody {
    private HttpStatus status;

    public ErrorBaseResponseBody(HttpStatus status, Object message) {
        super(message);
        this.status = status;
    }
}
