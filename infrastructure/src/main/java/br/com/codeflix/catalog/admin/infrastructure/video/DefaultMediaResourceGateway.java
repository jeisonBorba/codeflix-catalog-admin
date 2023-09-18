package br.com.codeflix.catalog.admin.infrastructure.video;

import br.com.codeflix.catalog.admin.domain.video.*;
import org.springframework.stereotype.Component;

@Component
public class DefaultMediaResourceGateway implements MediaResourceGateway {

    @Override
    public AudioVideoMedia storeAudioVideo(VideoID id, Resource resource) {
        return null;
    }

    @Override
    public ImageMedia storeImage(VideoID id, Resource resource) {
        return null;
    }

    @Override
    public void clearResources(VideoID id) {

    }
}
