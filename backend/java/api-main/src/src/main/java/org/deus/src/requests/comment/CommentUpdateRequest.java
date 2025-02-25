package org.deus.src.requests.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateRequest {
    @NotBlank(message = "The id cannot be empty")
    private String id;

    @NotBlank(message = "The songId cannot be empty")
    private String songId;

    @NotBlank(message = "The content cannot be empty")
    private String content;
}
