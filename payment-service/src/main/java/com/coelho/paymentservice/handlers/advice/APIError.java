package com.coelho.paymentservice.handlers.advice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class APIError {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(ZoneOffset.UTC);

    private String traceId;

    @Builder.Default
    private Collection<APIErrorMessage> messages = List.of();

    @JsonIgnore
    public Collection<Tag> toTags() {
        return Optional.ofNullable(messages)
                       .orElseGet(ArrayList::new)
                       .stream()
                       .map(value -> Tag.of("error", value.getCode()))
                       .collect(Collectors.toList());
    }
}
