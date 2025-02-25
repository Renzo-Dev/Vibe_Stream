package org.deus.src.services.models;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.ImageUrlsDTO;
import org.deus.src.dtos.fromModels.comment.CommentDTO;
import org.deus.src.dtos.fromModels.userProfile.ShortUserProfileDTO;
import org.deus.src.exceptions.action.ActionCannotBePerformedException;
import org.deus.src.exceptions.data.DataNotFoundException;
import org.deus.src.models.CommentModel;
import org.deus.src.models.SongModel;
import org.deus.src.models.UserProfileModel;
import org.deus.src.repositories.CommentRepository;
import org.deus.src.repositories.SongRepository;
import org.deus.src.repositories.UserProfileRepository;
import org.deus.src.requests.comment.CommentCreateRequest;
import org.deus.src.requests.comment.CommentUpdateRequest;
import org.deus.src.services.ImageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.deus.src.services.models.UserProfileService.getShortUserProfileDTO;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserProfileRepository userProfileRepository;
    private final SongRepository songRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    @Cacheable(value = "comment_dto", key = "#id")
    public CommentDTO getDTOById(UUID id) throws DataNotFoundException {
        CommentModel comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));

        UserProfileModel creatorUserProfile = comment.getCreatorUserProfile();

        return getCommentDTO(comment, creatorUserProfile, imageService);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "comments_dto_by_song", key = "#songId")
    public List<CommentDTO> getCommentsDTOBySongId(UUID songId) throws DataNotFoundException {
        SongModel song = songRepository
                .findById(songId)
                .orElseThrow(() -> new DataNotFoundException("Song not found"));

        return commentRepository
                .findBySong(song).stream()
                .map(commentModel -> {
                    UserProfileModel creatorUserProfile = commentModel.getCreatorUserProfile();

                    ImageUrlsDTO avatar = imageService.getAvatarForUser(creatorUserProfile.getUserId().toString());

                    ShortUserProfileDTO creatorUserProfileDTO = UserProfileModel.toShortDTO(creatorUserProfile, avatar);

                    return CommentModel.toDTO(commentModel, creatorUserProfileDTO);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "comments_by_song", key = "#request.songId"),
            @CacheEvict(value = "comments_dto_by_song", key = "#request.songId")
    })
    public CommentModel create(CommentCreateRequest request, UUID userId) throws DataNotFoundException {
        UserProfileModel creatorUserProfile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User Profile not found"));
        SongModel song = songRepository
                .findById(UUID.fromString(request.getSongId()))
                .orElseThrow(() -> new DataNotFoundException("Song not found"));

        CommentModel comment = new CommentModel();
        comment.setCreatorUserProfile(creatorUserProfile);
        comment.setSong(song);
        comment.setContent(request.getContent());

        CommentModel savedComment = commentRepository.save(comment);

        song.setNumberOfComments(song.getNumberOfComments() + 1);
        songRepository.save(song);

        return savedComment;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "comments_by_song", key = "#request.songId"),
            @CacheEvict(value = "comments_dto_by_song", key = "#request.songId")
    })
    public CommentModel update(CommentUpdateRequest request, UUID userId) throws DataNotFoundException, ActionCannotBePerformedException {
        UserProfileModel creatorUserProfile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User Profile not found"));

        CommentModel comment = commentRepository
                .findByIdAndCreatorUserProfile(UUID.fromString(request.getId()), creatorUserProfile)
                .orElseThrow(() -> new ActionCannotBePerformedException("Comment not found or you don't have permission to update it"));

        if (request.getContent() != null) {
            comment.setContent(request.getContent());
        }

        return commentRepository.save(comment);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "comments_by_song", key = "#songId"),
            @CacheEvict(value = "comments_dto_by_song", key = "#songId")
    })
    public void delete(UUID id, UUID songId, UUID userId) throws DataNotFoundException, ActionCannotBePerformedException {
        UserProfileModel creatorUserProfile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new DataNotFoundException("User Profile not found"));

        CommentModel comment = commentRepository
                .findByIdAndCreatorUserProfile(id, creatorUserProfile)
                .orElseThrow(() -> new ActionCannotBePerformedException("Comment not found or you don't have permission to delete it"));

        SongModel song = songRepository
                .findById(songId)
                .orElseThrow(() -> new DataNotFoundException("Song not found"));

        commentRepository.delete(comment);

        song.setNumberOfComments(song.getNumberOfComments() - 1);
        songRepository.save(song);
    }

    @NotNull
    public static CommentDTO getCommentDTO(CommentModel comment, UserProfileModel creatorUserProfile, ImageService imageService) {
        ShortUserProfileDTO creatorUserProfileDTO = getShortUserProfileDTO(creatorUserProfile, imageService);

        return CommentModel.toDTO(comment, creatorUserProfileDTO);
    }
}
