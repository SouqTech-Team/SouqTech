package org.stand.springbootecommerce.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageableResponse<T> {

    private long total;
    private List<T> list;

}
