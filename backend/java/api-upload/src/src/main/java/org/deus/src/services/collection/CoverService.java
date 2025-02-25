package org.deus.src.services.collection;

import lombok.RequiredArgsConstructor;
import org.deus.src.dtos.helpers.CoverConvertingDTO;
import org.deus.src.exceptions.StatusException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.message.MessageSendingException;
import org.deus.src.responses.SuccessMessageResponse;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.storage.StorageCoverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CoverService {
    private final StorageCoverService storageCoverService;
    private final RabbitMQService rabbitMQService;
    private static final Logger logger = LoggerFactory.getLogger(CoverService.class);

    public ResponseEntity<SuccessMessageResponse> coverUpload(String collectionId, String userId, MultipartFile cover) throws StatusException {
        String queueName = "convert.cover";
        String messageError = "";

        try {
            this.storageCoverService.putOriginalBytes(collectionId, cover.getBytes());

            this.rabbitMQService.sendCoverConvertingDTO("convert.cover", new CoverConvertingDTO(collectionId, userId));

            return ResponseEntity.ok(new SuccessMessageResponse("Cover upload process initiated. Please wait..."));
        }
        catch (IOException e) {
            messageError = "Failed to upload cover file!" + e.getMessage();
            logger.error(messageError, e);

            throw new StatusException("An error occurred while processing the uploaded cover file.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (DataSavingException e) {
            throw new StatusException("Failed to save the cover data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (MessageSendingException e) {
            throw new StatusException("Failed to send the CoverConvertingDTO to the message queue \"" + queueName + "\".", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            throw new StatusException(messageError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
