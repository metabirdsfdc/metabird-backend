package org.verse.metabird.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {

    @Id
    private String id;

    @Indexed
    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotNull(message = "Action must not be null")
    private Action action;

    @NotBlank(message = "Details must not be empty")
    private String details;

    @NotBlank(message = "Org must not be empty")
    @Indexed
    private String org;

    @Field("success")
    private boolean success;

    @CreatedDate
    private Instant createdAt;

}
