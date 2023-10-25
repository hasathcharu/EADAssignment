package org.adbms.orderplacement.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
public class RestException extends ResponseStatusException {

    public String msg;
    public RestException(HttpStatusCode status, String reason) {
        super(status, reason);
        this.setDetail(reason);
        this.setTitle(reason);
        this.setMsg(reason);
    }
}
