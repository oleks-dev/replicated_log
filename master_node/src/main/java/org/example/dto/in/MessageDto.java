package org.example.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageDto {
    @JsonProperty("write_concern")
    private int writeConcern;
    private String message;
}
