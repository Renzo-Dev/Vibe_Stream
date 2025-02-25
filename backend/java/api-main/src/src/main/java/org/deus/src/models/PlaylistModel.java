package org.deus.src.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deus.src.dtos.ImageUrlsDTO;
import org.deus.src.dtos.fromModels.playlist.PlaylistDTO;
import org.deus.src.dtos.fromModels.playlist.PublicPlaylistDTO;
import org.deus.src.dtos.fromModels.playlist.ShortPlaylistDTO;
import org.deus.src.dtos.fromModels.userProfile.ShortUserProfileDTO;
import org.deus.src.dtos.fromModels.userProfile.UserProfileDTO;
import org.deus.src.models.intermediateTables.likes.UserProfileLikedPlaylistModel;
import org.deus.src.models.intermediateTables.reposts.UserProfileRepostedPlaylistModel;
import org.deus.src.models.intermediateTables.PlaylistSongModel;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlists")
public class PlaylistModel extends CollectionModel {
    @Column(name = "description", length = 300)
    private String description;



    @JsonIgnore
    @OneToMany(mappedBy = "playlist")
    private Set<PlaylistSongModel> playlistSongs = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "playlist")
    private Set<UserProfileRepostedPlaylistModel> userProfilesReposted = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "playlist")
    private Set<UserProfileLikedPlaylistModel> userProfilesLiked = new HashSet<>();



    public static PlaylistDTO toDTO(PlaylistModel model, ShortUserProfileDTO creatorUserProfileDTO, ImageUrlsDTO cover) {
        return new PlaylistDTO(
                model.getId().toString(),
                model.getName(),
                model.getDuration(),
                model.getPrivacy(),
                model.getNumberOfSongs(),
                cover,
                model.getCreatedAt(),
                model.getUpdatedAt(),
                creatorUserProfileDTO,
                model.getDescription()
        );
    }

    public static PublicPlaylistDTO toPublicDTO(PlaylistModel model, ShortUserProfileDTO creatorUserProfileDTO, ImageUrlsDTO cover) {
        return new PublicPlaylistDTO(
                model.getId().toString(),
                model.getName(),
                model.getDuration(),
                model.getNumberOfSongs(),
                cover,
                creatorUserProfileDTO,
                model.getDescription(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }

    public static ShortPlaylistDTO toShortDTO(PlaylistModel model, ShortUserProfileDTO creatorUserProfileDTO, ImageUrlsDTO cover) {
        return new ShortPlaylistDTO(
                model.getId().toString(),
                creatorUserProfileDTO,
                model.getName(),
                cover,
                model.getCreatedAt()
        );
    }
}
