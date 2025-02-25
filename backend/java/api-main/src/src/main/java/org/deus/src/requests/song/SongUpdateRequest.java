package org.deus.src.requests.song;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongUpdateRequest {
    private String tempFileId;
    private String name;
    private Short placeNumber;
    private Float duration;
    private Set<Short> genreIds;
    private Set<String> tags;
}
